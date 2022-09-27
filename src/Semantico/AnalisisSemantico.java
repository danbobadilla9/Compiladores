/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Semantico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class AnalisisSemantico {
    public HashMap<Integer,HashMap> funcionesAll = new HashMap<Integer, HashMap>(); //Contiene las funciones y sus lineas de codigo
    public String[] codigoMain; //Codigo del main
    public String[][] funcionesReservadas; //Funciones reservadas 

    //Tabla de valores de las variables de main
    public HashMap<String,String> variablesTipo = new HashMap<String,String>();
    //Tabla de valores de las variables de funciones
    public List<HashMap<String,String>> variablesTipoFunciones = new ArrayList<HashMap<String,String>>();
    public List<HashMap<String,String>> variablesTipoFuncionesAux = new ArrayList<HashMap<String,String>>();
    public boolean bandera = false;
    
    public AnalisisSemantico(HashMap<Integer,HashMap> funcionesAll, List<String> codigoMain) {
        this.funcionesAll.putAll(funcionesAll);

        this.codigoMain = codigoMain.stream().toArray(String[]::new);
        this.funcionesReservadas = new String[][]{{"print","Void"},{"input","string"},{"int","int"}};
        primerAnalisis();
        segundoAnalisis();
    }
    
    //El primer analisis se basa en obtener datos de funciones reservadas
    public void primerAnalisis(){
        String exp1 = "^[a-zA-Z]+\\s*={1}\\s*input.+$";//Evalua un print
        for(int i = 0; i< codigoMain.length ; i ++){
            if(Pattern.compile(exp1).matcher(codigoMain[i]).matches()){ //Evaluamos un INPUT
                variablesTipo.put(codigoMain[i].substring(0, codigoMain[i].indexOf("=")).trim(), funcionesReservadas[1][1]);//Asignamos un string
            }//FALTA PONER UN INT
        }
//        mostrarDatos();
    }
    
    //El segundo analisis se basa en obtener parametros de las funciones apartir del MAIN
    public void segundoAnalisis(){
        String exp1 = "^[a-zA-Z]+\\s*\\({1}.+$"; //Evaluamos funciones sin retorno en el MAIN
        String exp2 = "^[a-zA-Z]+\\s*=\\s*[a-zA-Z]+\\({1}\\s*(((([a-zA-Z]+\\s*,(?=[a-zA-Z\\s])\\s*)|[a-zA-Z]+\\s*)|(int\\s*\\({1}\\s*[a-zA-Z]+\\s*\\){1}\\s*(,(?![,\\)])\\s*(?![,\\)]))*)|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*,\\s*(?!,\\)))|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*))*)\\){1}$"; //Evaluamos funciones con retornos en el MAIN
        String exp3 = "^[a-zA-Z]+\\s*=\\s*[a-zA-Z]+\\({1}\\s*(((([a-zA-Z]+\\s*,(?=[a-zA-Z\\s])\\s*)|[a-zA-Z]+\\s*)|([a-zA-Z]+\\({1}\\s*[a-zA-Z]+\\s*\\){1}\\s*(,(?![,\\)])\\s*(?![,\\)]))*)|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*,\\s*(?!,\\)))|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*))*)\\){1}$";
        boolean existeFuncion = false;
        for(int i = 0; i < codigoMain.length; i++){
            if(Pattern.compile(exp1).matcher(codigoMain[i]).matches()){ //FUNCIONES SIN RETORNO
                String nameFuncion = codigoMain[i].substring(0,codigoMain[i].indexOf("(")).trim();
                for( HashMap<String, String> funcion : funcionesAll.values()){
                    if(nameFuncion.equals(funcion.get("name"))){ //Verificamos que exista una funcion llamada asi 
                        existeFuncion = true;
                        initTablaValores(funcion,nameFuncion); //Asignando Void a las lineas
                        //Contando el numero de parametros y retorno
                        if(!this.bandera){
//                            System.out.println("PASA" + codigoMain[i]);
                            configuracionParametros(nameFuncion,funcion.get("parametros"),codigoMain[i],funcion.get(String.valueOf(funcion.size()-2)),true);
                        }else{
                            //FALTA VALIDAR QUE NO PONGAN OTRO TIPO DE DATO 
                        }
                            
                    }
                }
                if (nameFuncion.equals("print")) {
                    existeFuncion = true;
                }
                if(!existeFuncion){ //Si no hay una función llamada asi manda error
                    errorMensaje("La función: "+nameFuncion+" No esta Definida", codigoMain[i]);
                }
                
            }else if(Pattern.compile(exp2).matcher(codigoMain[i]).matches() || Pattern.compile(exp3).matcher(codigoMain[i]).matches()){//Funciones con RETORNO y ASIGNACION de variable 
                //Revisamos que la variable no haya sido instanciada previamente
//                System.out.println(codigoMain[i]);
                if(variablesTipo.containsKey(codigoMain[i].substring(0, codigoMain[i].indexOf("=")).trim())){//Variable ya instanciada
                    //Obtenemos a que funcion pertenece 
                    String nameFuncion = codigoMain[i].substring(codigoMain[i].indexOf("=")+1,codigoMain[i].indexOf("(") ).trim();
                    for (HashMap<String, String> funcion : funcionesAll.values()) {
                        if (nameFuncion.equals(funcion.get("name"))) { //Verificamos que exista una funcion llamada asi 
                            existeFuncion = true;
                            initTablaValores(funcion, nameFuncion); //Asignando Void a las lineas
                            //Contando el numero de parametros y retorno
                            if (!bandera) {
                                configuracionParametros(nameFuncion, funcion.get("parametros"), codigoMain[i], funcion.get(String.valueOf(funcion.size() - 2)),false);
                                variablesTipo.replace(codigoMain[i].substring(0, codigoMain[i].indexOf("=")).trim(), variablesTipo.get(codigoMain[i].substring(0, codigoMain[i].indexOf("=")).trim())+","+getValueFuncion(nameFuncion));
                            } else {
                                //FALTA VALIDAR QUE NO PONGAN OTRO TIPO DE DATO 
                            }

                        }
                    }
                    
                    if (nameFuncion.equals("input")) {
                        existeFuncion = true;
                    }
                    if (!existeFuncion) { //Si no hay una función llamada asi manda error
                        errorMensaje("La función: " + nameFuncion + " No esta Definida", codigoMain[i]);
                    }
                }else{//Variable que NO ESTA INSTANCIADA
                    
                    String nameVariableMain = codigoMain[i].substring(0,codigoMain[i].indexOf("=")); //Variable a ser instanciada en el main
                    String nameFuncion = codigoMain[i].substring(codigoMain[i].indexOf("=")+1,codigoMain[i].indexOf("(") ).trim(); // Obtenemos el nombre de la funcion que se utiliza
                    for (HashMap<String, String> funcion : funcionesAll.values()) {
//                        System.out.println(funcion);
//                        System.out.println("aca->"+codigoMain[i]+" nmaefUNCION->"+nameFuncion+" funcion->"+funcion.get("name")+" contains->"+funcion.containsValue(nameFuncion));

                        if (nameFuncion.equals(funcion.get("name"))) { //Verificamos que exista una funcion llamada asi 
                            existeFuncion = true;
                            initTablaValores(funcion, nameFuncion); //Asignando Void a las lineas
                            //Contando el numero de parametros y retorno
                            if (!bandera) {
                                configuracionParametros(nameFuncion, funcion.get("parametros"), codigoMain[i], funcion.get(String.valueOf(funcion.size() - 2)), false);
                                variablesTipo.put(codigoMain[i].substring(0, codigoMain[i].indexOf("=")).trim(),getValueFuncion(nameFuncion));
                            } else {
                                //FALTA VALIDAR QUE NO PONGAN OTRO TIPO DE DATO 
                            }
//                            break;
                        }
                    }
                    if (nameFuncion.equals("input")) {
                        existeFuncion = true;
                    }
                    if (!existeFuncion) { //Si no hay una función llamada asi manda error
                        errorMensaje("La función: " + nameFuncion + " No esta Definida", codigoMain[i]);
                    }
                }
            }
        }
        mostrarDatos();
    }
    
    public void initTablaValores(HashMap<String, String> funcion,String nameFuncion){
        if(variablesTipoFunciones.isEmpty()){//Si el arreglo esta vacio se agrega 
            HashMap<String, String> informacionFuncion = new HashMap<String, String>();
                for (String data : funcion.values()) {
                    informacionFuncion.put(data, "void");
                }
                variablesTipoFunciones.add(informacionFuncion);
        }else{ //Verificamos que la funcion ya este almacenada
            boolean anidar = true;
            for (HashMap<String, String> buscarFuncion : variablesTipoFunciones) {
                if (buscarFuncion.containsKey(nameFuncion.trim())) { //Si no esta registrado, se registra
                    anidar = false;
                }
            }
            if(anidar){
                HashMap<String, String> informacionFuncion = new HashMap<String, String>();
                for (String data : funcion.values()) {
                    informacionFuncion.put(data, "void");
                }
                variablesTipoFuncionesAux.add(informacionFuncion);
                variablesTipoFunciones.addAll(variablesTipoFuncionesAux);
                variablesTipoFuncionesAux.clear();
                this.bandera = false;
            }else{
                this.bandera = false;
            }
        }
    }
    
    public void configuracionParametros(String nameFuncion,String parametros, String lineaCodigo,String retorno,boolean banderaRetorno){
        this.bandera = true;
        //Asignamos valores a los parametros de la funcion
        String[] parametrosEnv = lineaCodigo.substring(lineaCodigo.indexOf("(")+1, lineaCodigo.indexOf(")")).split(",");
        if(parametros.split(",").length == parametrosEnv.length){//Si el numero de parametros es el mismo
            asignarTipoParametros(parametros, parametrosEnv, nameFuncion,lineaCodigo);
            //Evaluamos si la funcion tiene un retorno
            if(retorno.contains("return")&&banderaRetorno){
                errorMensaje("La función retorna un valor: ", lineaCodigo);
            }
//            else{
//                variablesTipoFunciones.forEach( buscador -> {
//                    if(buscador.containsKey(nameFuncion)){
//                        buscador.replace(nameFuncion, "");
//                    }
//                });
//            }
        }else{
            errorMensaje("Error en la cantidad de parametros pasados a la función", lineaCodigo);
        }
    }
    
    public void asignarTipoParametros(String paramesFunc,String[] parametrosEnv,String nameFuncion,String lineaCodigo){
        String exp1 = "^\"{1}[a-zA-Z\\s0-9:]+\"{1}$"; //Tipo de dato String "dato"
        String exp2 = "^[a-zA-Z0-9]+$"; //Tipo de dato en variable -> dato
        String exp3 = "^[a-zA-Z0]+\\({1}\\s*[a-zA-Z]+\\s*$"; //Evaluamos que tenga una funcion dentro
        String asignarTipo = "";
        for(String parametro: parametrosEnv){
            if(Pattern.compile(exp1).matcher(parametro).matches()){
                asignarTipo+="string";
            }else if(Pattern.compile(exp2).matcher(parametro).matches()){
                if(variablesTipo.containsKey(parametro)){//Buscamos que sea variable ya generada en el main
                    if(variablesTipo.get(parametro).contains(",")){
                        asignarTipo+=variablesTipo.get(parametro).substring(variablesTipo.get(parametro).indexOf(",")+1);
                        asignarTipo+=" ";
                        continue;
                    }
                    asignarTipo+=variablesTipo.get(parametro);

                }else{
                    errorMensaje("No esta definida la variable: "+parametro.substring(0,parametro.indexOf("("))+" ", lineaCodigo);
                }
                
            }else if(Pattern.compile(exp3).matcher(parametro).matches()){ //Una funcion dentro como parametro 
                boolean bandera = false;
                String parametroAux = parametro.substring(parametro.indexOf("(")+1 );
                if (!variablesTipo.containsKey(parametroAux)) {//Buscamos que sea variable ya generada en el main
                    errorMensaje("No esta definida la variable: " + parametroAux + " ", lineaCodigo);
                } 
                for(String[] funcionReservada: funcionesReservadas){//Buscamos que sea funcion ya reservada
                    if(funcionReservada[0].equals(parametro.substring(0,parametro.indexOf("(")))){
                      asignarTipo += funcionReservada[1];
                      bandera = true;
                    }
                }
                if(!bandera){//Buscamos que exista una funcion definida por el usuario 
                    for (HashMap<String, String> data : variablesTipoFunciones) {
                        if(data.containsKey(parametro.substring(0,parametro.indexOf("(")))){
                            asignarTipo += data.get(parametro.substring(0,parametro.indexOf("(")));
                        }
                    }
                    errorMensaje("No esta definida la funcion: "+parametro.substring(0,parametro.indexOf("("))+" ", lineaCodigo);
                }
            }//AQUI VA OTRA VALIDACION DE OTRO TIPO DE DATO
            asignarTipo+=" ";
        }
       asignarTipo=asignarTipo.replaceFirst(" ", ",");
       asignarTipo=asignarTipo.replaceAll(" ", "");
       
       //Asignamos el tipo de dato que les corresponde 
       String newValue = paramesFunc+" "+asignarTipo;
        variablesTipoFunciones.forEach(buscador -> {
            if (buscador.containsKey(nameFuncion)) {
                buscador.remove(paramesFunc);
                buscador.put("parametros", newValue);
            }
        });
        analisisFuncion(nameFuncion,lineaCodigo);
        
    }
    
    public void analisisFuncion(String nameFuncion,String lineaCodigo){
        int indiceFuncion = 0;
        String[] parametros;
        //Obteniendo la funcion original y extrañendola para iterar sobre ella
        HashMap<String, String> funcionOriginal = new HashMap<String, String>();
        for (HashMap<String, String> funcion : funcionesAll.values()) {
            if(nameFuncion.equals(funcion.get("name"))){
                funcionOriginal.putAll(funcion); 
            }
        }
        
        //Obteniendo un elemento y eliminando para posteriormente insertarlo en su indice correspondiente 
        HashMap<String, String> funcionData = new HashMap<String, String>();
        for (HashMap<String, String> data : variablesTipoFunciones) {
            if(data.containsKey(nameFuncion)){
                indiceFuncion = variablesTipoFunciones.indexOf(data);
                funcionData = data;
            }
        }
        variablesTipoFunciones.remove(indiceFuncion);
        System.out.println("PARAMETROS ->: "+funcionData.get("parametros"));
        parametros = funcionData.get("parametros").split(" ");
        //INICIANDO EL ANALISIS
        //Primero eliminaremos las lineas de codigo como name, parametros ya que no nos interesan
        funcionOriginal.remove("name");
        funcionOriginal.remove("parametros");
        //Config de los espacios dentro del for
        int space = 4;
        boolean bandera = false; // SI un ciclo for esta vacio regresa un error
        String auxLinea = "";//Guardamos la linea cuando tenemos algo como -> name_va= name_va
        boolean banderaLinea = false;
        String exp1 = "^\\s+print\\({1}.+$"; //Funciones ya conocidas 
        String exp2 = "^\\s+[a-zA-Z]+\\s*={1}\\s*\\[{1}\\]{1}$"; //Se detecta que tenemos una asignacion de arreglo
        String exp3 = "^\\s+for[a-zA-Z\\s]+in\\s+range.+$"; //Controlador de un for
        String exp4 = "^\\s+[a-zA-Z]+\\s*={1}\\s*((int\\s*\\({1}.+)|(input\\s*\\({1}.+))$"; // Creacion de variable con funciones del main
        String exp5 = "^\\s*[a-zA-Z]+\\.{1}append+\\({1}\\s*[a-zA-Z]+\\s*\\){1}$"; // Aggregando elementos al array
        String exp6 = "^\\s+return\\s+[a-zA-Z]+$"; //Evalua El Return
        String exp7 = "^\\s+[a-zA-Z]+\\s*={1}\\s*[0-9]+$";//Se detecta que tenemos una variable de tipo int
        String exp8 = "^\\s+[a-zA-Z]+\\s*={1}[a-zA-Z]+\\s*\\+{1}\\s*[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}$";//Suma de un int con un arreglo
        String exp9 = "^\\s+[a-zA-Z]+\\s*={1}[a-zA-Z]+\\s*\\[{1}[0-9]\\]{1}$";//Inicializa variable apartir de un arreglo lista[0]
        String exp10 = "^\\s+[a-zA-Z]+\\s*={1}\\s*[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}\\*{1}[a-zA-Z]+\\s?$";//Multiplicar un arreglo y un int 
        for(String linea: funcionOriginal.values()){
            //Controlando los espacios 
            if(contadorEspacios(linea,bandera,space) > space ){
                errorMensaje("Error en la identación: ", linea);
            }
            bandera = false;
            
            if(Pattern.compile(exp1).matcher(linea).matches()){ //Saltamos las funciones ya conocidas 
                continue;
            }
            if(Pattern.compile(exp2).matcher(linea).matches()){ //Si es un arreglo
                funcionData.replace(linea, "Void-Array");
            }
            //Iniciamos el proceso de recorrer un for, validacion de identacion
            if(Pattern.compile(exp3).matcher(linea).matches()){
                funcionData.replace(linea, "Ciclo-For");
                space+=4;
                bandera = true;
            }
            if(Pattern.compile(exp4).matcher(linea).matches()){ //La funcion pertenece a las del main
                String funcionBuscar = linea.substring(linea.indexOf("=")+1, linea.indexOf("(")).replaceAll(" ", "");
                //Busqueda de la funcion
                for(String[] funciones: funcionesReservadas){
                    if(funcionBuscar.equals(funciones[0])){
                        funcionData.replace(linea, funciones[1]);
                    }
                }
            }
            if(Pattern.compile(exp5).matcher(linea).matches()){//Controlando añadir elementos a un arreglo
                //Primero conseguimos la variable y el parametro
                String nameVariable = linea.substring(linea.indexOf("[a-zA-Z]")+1, linea.indexOf(".")).trim();
                String nameParametro = linea.substring(linea.indexOf("(")+1, linea.indexOf(")")).trim();
                String valueParametro = "void";
                for(String key: funcionData.keySet()){
                    if(key.contains("=")){
                        if(key.substring(key.indexOf("[a-zA-Z]")+1, key.indexOf("=")).trim().equals(nameParametro)){
                            if(!funcionData.get(key).trim().equals(valueParametro)){
                                valueParametro = funcionData.get(key);
                            }else{
                                errorMensaje("1.- No se a inicializado la variable: "+nameParametro+" ", linea);
                            }
                        }
                    }

                    if(funcionData.get(key).equals("Void-Array")){
                        if(key.substring(key.indexOf("[a-zA-Z]")+1, key.indexOf("=")).trim().equals(nameVariable)){
                            funcionData.replace(linea,"Append"); //Añadiendo elementos
                            if(valueParametro.equals("void"))
                                errorMensaje("No se a inicializado la variable: "+nameParametro+" ", linea);
                            String valueArray = funcionData.get(key).replace("Void", valueParametro);
                            funcionData.replace(key, valueArray);
                        }
                    }
                }
            }
            if(Pattern.compile(exp6).matcher(linea).matches()){//Control del Return
                String nameVariable = linea.substring(linea.indexOf("n")+1, linea.length()).trim();
                String valueParametro = "void";
                for (String key : funcionData.keySet()) {
                    if (key.contains("=")) {
                        if(banderaLinea){
                            if (key.substring(key.indexOf("[a-zA-Z]") + 1, key.indexOf("=")).trim().equals(nameVariable) && key.equals(auxLinea)) {
                                if (!funcionData.get(key).trim().equals(valueParametro)) {
                                    valueParametro = funcionData.get(key);
                                    banderaLinea = false; auxLinea = "";
                                }else {
                                    errorMensaje("No se a inicializado la variable: " + nameVariable + " ", linea);
                                }
                            }   
                        }else if (key.substring(key.indexOf("[a-zA-Z]") + 1, key.indexOf("=")).trim().equals(nameVariable)) {
                            if (!funcionData.get(key).trim().equals(valueParametro)) {
                                valueParametro = funcionData.get(key);
                            }else {
                                errorMensaje("No se a inicializado la variable: " + nameVariable + " ", linea);
                            }
                        }
                    }
                }
                funcionData.replace(nameFuncion, valueParametro);
            }
            if(Pattern.compile(exp7).matcher(linea).matches()){
                funcionData.replace(linea, "int");
            }
            if(Pattern.compile(exp8).matcher(linea).matches()){
                String nameVariable = linea.substring(linea.indexOf("[a-zA-Z]")+1, linea.indexOf("=")).trim();
                String sumando1 = linea.substring(linea.indexOf("=")+1, linea.indexOf("+")).trim();
                String sumando2 = linea.substring(linea.indexOf("+")+1).trim();
                String valueSumando1 = "void",valueSumando2 = "void";
                String expAux = "^[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}$";//Verifica que sea un arreglo si no se realiza otro tipo de tratamiento
                if(Pattern.compile(expAux).matcher(sumando2).matches()){//Si es un arreglo NECESITA UNA VALIDACION PARA EL CASO -> name_v= name_v
                    //Verificamos que sea una variable como parametro
                    if (funcionData.get("parametros").split(" ")[0].equals(sumando1)) {
                        valueSumando1 = funcionData.get("parametros").split(" ")[1].substring(0, funcionData.get("parametros").split(" ")[1].indexOf("-")).trim();
                    } else if (funcionData.get("parametros").split(" ")[0].equals(sumando2.substring(0,sumando2.indexOf("[")))) {
                        valueSumando2 = funcionData.get("parametros").split(" ")[1].substring(0, funcionData.get("parametros").split(" ")[1].indexOf("-")).trim();
                    }
                    //Verificamos que la variable de sumando1 y sumando2 ya este instanciada 
                    for(String key: funcionData.keySet()){
                        if(key.contains("=")){
                            if(key.substring(key.indexOf("[a-zA-Z]")+1, key.indexOf("=")).trim().equals(sumando1) && !key.equals(linea)){
                                valueSumando1 = funcionData.get(key);
                            }else if(key.substring(key.indexOf("[a-zA-Z]")+1, key.indexOf("=")).trim().equals(sumando2.substring(0,sumando2.indexOf("["))) && !key.equals(linea)){
                                valueSumando2 = funcionData.get(key);
                            }
                        }
                    }
                    if(valueSumando1.equals("void") || valueSumando2.equals("void")){ //Si una variable no fue inicializada manda error
                        errorMensaje("Una de las variables no esta inicializada: ", linea);
                    }
                    if(!valueSumando1.equals(valueSumando2)){//Si son tipos de datos diferentes manda error 
                        errorMensaje("Error de tipo de dato: ", linea);
                    }else{
                        funcionData.replace(linea, valueSumando1);
                        auxLinea = linea;
                        banderaLinea = true;
                    }
                }//TRATAMIENTO DIFERENTE 
            }
            if(Pattern.compile(exp9).matcher(linea).matches()){//Creamos una variable apartir de un arreglo
                //Buscamos el arreglo dentro de la funcion primero dentro del parametro 
                String iniciador = linea.substring(linea.indexOf("=")+1,linea.indexOf("["));
                String valueIniciador = "void";
                if(funcionData.get("parametros").split(" ")[0].equals(iniciador)){ //Buscamos si es un parametro de la funcion 
                    valueIniciador = funcionData.get("parametros").split(" ")[1].substring(0, funcionData.get("parametros").split(" ")[1].indexOf("-")).trim();
                }
                for (String key : funcionData.keySet()) {//Buscamos la variable dentro de la funcion 
                    if (key.contains("=")) {
                        if (key.substring(key.indexOf("[a-zA-Z]") + 1, key.indexOf("=")).trim().equals(iniciador) && !key.equals(linea)) {//Evitamos que sea la misma linea ya que tendremos void 
                            valueIniciador = funcionData.get(key);
                        } 
                    }
                }
                if (valueIniciador.equals("void")) { //Si una variable no fue inicializada manda error
                    errorMensaje("La variable: "+iniciador+" No esta instanciada ", linea);
                }else{
                    funcionData.replace(linea, valueIniciador);
                }
            }
            if(Pattern.compile(exp10).matcher(linea).matches()){
                String nameVariable = linea.substring(linea.indexOf("[a-zA-Z]") + 1, linea.indexOf("=")).trim();
                String sumando2 = linea.substring(linea.indexOf("=") + 1, linea.indexOf("*")).trim();
                String sumando1 = linea.substring(linea.indexOf("*") + 1).trim();
                String valueSumando1 = "void", valueSumando2 = "void";
                String expAux = "^[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}$";//Verifica que sea un arreglo si no se realiza otro tipo de tratamiento
                if (Pattern.compile(expAux).matcher(sumando2).matches()) {//Si es un arreglo NECESITA UNA VALIDACION PARA EL CASO -> name_v= name_v
                    //Verificamos que sea una variable como parametro
                    if (funcionData.get("parametros").split(" ")[0].equals(sumando1)) {
                        valueSumando1 = funcionData.get("parametros").split(" ")[1].substring(0, funcionData.get("parametros").split(" ")[1].indexOf("-")).trim();
                    } else if (funcionData.get("parametros").split(" ")[0].equals(sumando2.substring(0, sumando2.indexOf("[")))) {
                        valueSumando2 = funcionData.get("parametros").split(" ")[1].substring(0, funcionData.get("parametros").split(" ")[1].indexOf("-")).trim();
                    }
                    //Verificamos que la variable de sumando1 y sumando2 ya este instanciada 
                    for (String key : funcionData.keySet()) {
                        if (key.contains("=")) {
                            if (key.substring(key.indexOf("[a-zA-Z]") + 1, key.indexOf("=")).trim().equals(sumando1) && !key.equals(linea)) {
                                valueSumando1 = funcionData.get(key);
                            } else if (key.substring(key.indexOf("[a-zA-Z]") + 1, key.indexOf("=")).trim().equals(sumando2.substring(0, sumando2.indexOf("["))) && !key.equals(linea)) {
                                valueSumando2 = funcionData.get(key);
                            }
                        }
                    }
                    if (valueSumando1.equals("void") || valueSumando2.equals("void")) { //Si una variable no fue inicializada manda error
                        errorMensaje("Una de las variables no esta inicializada: ", linea);
                    }
                    if (!valueSumando1.equals(valueSumando2)) {//Si son tipos de datos diferentes manda error 
                        errorMensaje("Error de tipo de dato: ", linea);
                    } else {
                        funcionData.replace(linea, valueSumando1);
                        auxLinea = linea;
                        banderaLinea = true;
                    }
                }//TRATAMIENTO DIFERENTE 
            }
        }
        
        variablesTipoFunciones.add(indiceFuncion, funcionData);
    }
    
    public int contadorEspacios(String linea, boolean bandera, int space){
        int contador = 0;
        for(int i = 0; i < linea.length(); i++){
            if(linea.charAt(i) == ' '){
                contador ++;
            }else{
                break;
            }
        }
        if(bandera){
            if(space != contador){
                errorMensaje("No puede haber un for vacio!! ", linea);
            }
        }
        return contador;
    }
    
    public String getValueFuncion(String nameFuncion){
        String dataValue = "";
        for (HashMap<String, String> data : variablesTipoFunciones) {
            if(data.containsKey(nameFuncion)){
                dataValue = data.get(nameFuncion);
            }
        }
        return dataValue;
    }
        public void mostrarFunciones(){
        for (HashMap<String, String> funcion : funcionesAll.values()) {
            System.out.println(funcion);
            System.out.println("\n");
            for (String datos : funcion.values()) {
                System.out.println(datos);
            }
        }
    }
    public void mostrarDatos(){
        System.out.println("VARIABLES MAIN");
        for(String key: variablesTipo.keySet() ){
            System.out.println("KEY -> "+key+" VALUE -> "+variablesTipo.get(key));
        }
        System.out.println("DATA FUNCIONES ");
        for(HashMap<String,String> data: variablesTipoFunciones){
            for(String key: data.keySet()){
                System.out.println("KEY ->"+key+" VALUE -> "+data.get(key));
            }
            System.out.println("");
        }
        
    }
    
    public void errorMensaje(String mensaje,String linea){
        //Vamos a leer el archivo denuevo 
        System.out.println(mensaje +"Linea: "+linea);
        System.exit(0);
    }
}
