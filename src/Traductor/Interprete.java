package Traductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Interprete {
    
    public HashMap<Integer,HashMap> funcionesAll = new HashMap<Integer, HashMap>(); //Contiene las funciones completas originales 
    public HashMap<String,String> variablesTipo = new HashMap<String,String>(); //Contiene las variables instanciadas del main y su tipo de dato
    public HashMap<String,String> variablesTipoFuncion = new HashMap<String,String>(); //Contiene las variables instanciadas de funciones y su tipo de dato
    public List<HashMap<String,String>> variablesTipoFunciones = new ArrayList<HashMap<String,String>>(); //Contiene las funciones completas pero ya su tipo de dato
    public List<String> sobrecargaFunciones = new ArrayList<String>(); //Contiene las funciones que son sobrecargadas 
    public String[] codigoMain; //Codigo del main
    public String[][] funcionesReservadas = new String[][]{{"print","Void"},{"input","string"},{"int","int"}}; //Funciones reservadas
    
    //HasMap Controla la creacion de variables a las que se les vuelve asignar otro tipo de dato
    public HashMap<String,HashMap<String,String>> variableRepetida = new HashMap<String,HashMap<String,String>>();
    public HashMap<String,Integer> banderaFunciones = new HashMap<String,Integer>(); //Controla las funciones ya creadas
    public String auxiliarNameFuncion = "";
    public int space = 4;
    public HashMap<String,HashMap<String,String>> variableRepetidaFunciones = new HashMap<String,HashMap<String,String>>();//HasMap Controla la creacion de variables a las que se les vuelve asignar otro tipo de dato dentro de una funcion
    
    //Formando el codigo traducido
    public List<String> encabezadoLibreria = new ArrayList<String>(); //Librerias de C++
    public List<String> codigoPrincipal = new ArrayList<String>(); //Codigo principal de C++
    public List<String> instanciaVariable = new ArrayList<String>(); //Creacion de variables de C++ en el codigo MAIN
    public List<String> instanciaFunciones = new ArrayList<String>(); //Creacion de funciones de C++ en el codigo MAIN
    public List<String> funciones = new ArrayList<String>(); //Auxiliar de instanciaFunciones
            
    public Interprete(HashMap<Integer,HashMap> funcionesAll,List<String> codigoMain,HashMap<String,String> variablesTipo,List<HashMap<String,String>> variablesTipoFunciones,List<String> sobrecargaFunciones) {
        this.funcionesAll.putAll(funcionesAll);
        this.codigoMain = codigoMain.stream().toArray(String[]::new);
        this.variablesTipo.putAll(variablesTipo);
        this.variablesTipoFunciones = variablesTipoFunciones;
        this.sobrecargaFunciones = sobrecargaFunciones;
    }
    
    public void interpretacion (){
        //Agregamos las librerias de C++
        setLibrerias("#include<iostream>");
        setLibrerias("using namespace std");
        
        //Creamos la clase int main(){}
        setCodigoMain("int main(){\n");
        for(String linea: codigoMain){
            leerLineas(linea,true);
        }
        //Agregamos la creacion de variables
        codigoPrincipal.addAll(1,instanciaVariable);
        codigoPrincipal.addAll(0,instanciaFunciones);
        //Agregamos el final del cierre de la clase int main()
        setCodigoMain("return 0;");
        setCodigoMain("\n}");
        mostrarData();
//        mostrarDatosFunciones();
    }
    
    public void setLibrerias(String libreria){
        encabezadoLibreria.add(libreria);
    }
    public void setCodigoMain(String codigo){
        codigoPrincipal.add(codigo);
    }
    
    public void setAuxiliarFunciones(String codigo){
        funciones.add(codigo);
    }
        
    public void leerLineas(String linea, boolean bandera){
        //Expresiones regulares que nos ayudaran a guiarnos en que parte estamos 
        String exp1 = "^\\s*print\\s*.+$"; //Evalua un print
        String exp2 = "^\\w+\\s*={1}\\s*input.+$"; //Evalua una asignacion que lleva un input
        String exp3 = "^[a-zA-Z0-9]+\\s*\\({1}.+$"; //Evalua llamadas a funciones desde el main sin retorno
        String exp4 = "^\\s*[a-zA-Z0-9]+\\s*=\\s*(?!((input)|(int)))\\s*[a-zA-Z0-9]+\\s*\\({1}(?!input).+$"; // Evalua la asignacion a una variable invocando una funcion

        //Expresiones regulares dentro de las funciones
        String exp5 = "^\\s*[a-zA-Z0-9]+\\s*=\\s*\\[\\]$"; //Instancia de un arreglo de la forma -> var = []
        String exp6 = "^\\s*for\\s*[a-zA-Z0-9]\\s*in\\s*range\\s*\\({1}.+$"; //Evalua un for
        
        //Auxiliares para evaluar un for
        
        if(Pattern.compile(exp1).matcher(linea).matches()){
            generarCount(linea,bandera);
            return;
        }

        if(Pattern.compile(exp2).matcher(linea).matches()){//AUN NO LE PASAMOS LA BANDERA 
            //Cambiamos el nombre de la variable si se vuelve a asignar 
            setVariableRepetida(linea.substring(0,linea.indexOf("=")),linea,bandera);
        }

        if(Pattern.compile(exp3).matcher(linea).matches()){
            instanciarFuncionesMain(linea); //Agregamos la linea de codigo que llama a la funcion en el MAIN
            if(!banderaFunciones.containsKey(linea.substring(0,linea.indexOf("(")))){//Creamos las funciones y su sobrecarga
                generarFunciones(linea.substring(0,linea.indexOf("(")));
            }
        }
        if(Pattern.compile(exp4).matcher(linea).matches()){
            //Instanciamos la variable o cambiamos nombre en caso de que ya este instanciada
            setVariableRepetida(linea.substring(0,linea.indexOf("=")), linea,bandera);
            if(!banderaFunciones.containsKey(linea.substring(0,linea.indexOf("(")))){//Creamos las funciones y su sobrecarga
                generarFunciones(linea.substring(linea.indexOf("=")+1,linea.indexOf("(")));
            }
            return;
        }
        
        if(Pattern.compile(exp5).matcher(linea).matches()){
            setVariableRepetida(linea.substring(0,linea.indexOf("=")),linea,bandera);
        }
        
        if(Pattern.compile(exp6).matcher(linea).matches()){
            this.space*=2;
            setInstanciarFor(linea,bandera);
            return;
        }
        
        //Contador de espacios para reventar el for
        if((contadorEspacios(linea) < this.space) && !bandera && this.space > 5){
            setAuxiliarFunciones("}");
            this.space = this.space - 4;
        }
    }
    
    public void mostrarDatosFunciones(){
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
        System.out.println("FUNCIONES SOBRECARGADAS");
        for(String sobrecargada: sobrecargaFunciones){
            System.out.println(sobrecargada);
        }
        System.out.println("FUNCIONES ALL \n");
        for(int key: funcionesAll.keySet()){
            System.out.println(funcionesAll.get(key));
        }
        System.out.println("\n");
    }
    
    public void mostrarData(){
        System.out.println("LIBRERIAS \n");
        for(String linea: encabezadoLibreria){
            System.out.println(linea);
        }
        System.out.println("\n CODIGO MAIN\n");
        for(String linea: codigoPrincipal){
            System.out.println(linea);
        }
        System.out.println("\n");
    }
    public void generarCount(String linea ,boolean bandera){
        linea = linea.substring(linea.indexOf("(")+1,linea.lastIndexOf(")"));
        if(linea.contains(",")){
            String[] lineas = linea.split(",");
            String aux = "count";
            for(String parametro: lineas){
                if(parametro.contains("\"")){
                    aux += " << "+ parametro+" ";
                }else{
                    aux += " << \" \" << "+parametro;
                }
            }
            aux += " << \" \\n \"";
            if(bandera){
                setCodigoMain(aux+";");
            }else{
                setAuxiliarFunciones("    "+aux+";");
            }
            
        }else{
            if(bandera){
                setCodigoMain("count << "+linea+" << \" \\n \" ;");
            }else{
                setAuxiliarFunciones("    count << "+linea+" <<\" \\n \";");
            }
            
        }
    }
    
    public void setVariableRepetida(String variable,String lineaCodigo,boolean bandera){
        HashMap<String,String> replaceData = new HashMap<>();
        //HasMap que nos ayudaran a recorrer los arreglos
        HashMap<String,HashMap<String,String>> variablesRepetidas = new HashMap<>();
        HashMap<String,String> variablesTipos = new HashMap<>();
        //Instanciamos los HasMap
        if(bandera){//Si bandera es true estamos en el main
            variablesRepetidas = variableRepetida;
            variablesTipos = variablesTipo;
        }else{//Estamos en la funcion
            variablesRepetidas = variableRepetidaFunciones;
            variablesTipoFuncion.putAll(getDataFuniones(this.auxiliarNameFuncion));
            variablesTipos = variablesTipoFuncion;
        }
        //Primero Preguntamos si ya esta registrada
        if(variablesRepetidas.containsKey(variable)){
            String tipoDato = variablesTipos.get(variable).split(",")[Integer.valueOf(variablesRepetidas.get(variable).get("i")+1)];
            String tipoDato2 = variablesTipos.get(variable).split(",")[Integer.valueOf(variablesRepetidas.get(variable).get("i"))];
            HashMap<String,String> addVariable = variablesRepetidas.get(variable);
            if(addVariable.containsKey(tipoDato)){//Verificamos que el tipo de dato ya este registrado
                String nameVariable = addVariable.get("oldName") +lineaCodigo.substring(lineaCodigo.indexOf("=")+1);
                if(bandera){
                    setCodigoMain(nameVariable+";");
                }else{
                    setAuxiliarFunciones(nameVariable+";");
                }
            }else{//Tipo de dato diferente
                String newName= variable+""+generarVariableUnica(variable);
                replaceData.putAll(variablesRepetidas.get(variable));
                replaceData.put(tipoDato, newName);
                replaceData.replace("i", String.valueOf(Integer.valueOf(variablesRepetidas.get(variable).get("i"))+1));
                replaceData.replace("oldName", variablesRepetidas.get(variable).get(tipoDato2));
                variablesRepetidas.replace(variable, replaceData);
                instanciarVariables(variable,bandera,lineaCodigo);
                String aux = "";
                if(bandera){
                    if(lineaCodigo.contains("\"")){
                        aux = lineaCodigo.substring(lineaCodigo.indexOf("=")+1);
                    }else{
                        aux = lineaCodigo.substring(lineaCodigo.indexOf("=")+1).replaceAll("Datos", replaceData.get("oldName"));
                    }
                    setCodigoMain(newName+"="+aux);
                }else{
                    setAuxiliarFunciones(lineaCodigo+";");//Escribe la linea de codigo
                }
            }
        }else{ //Si la variable no esta registrada se crea
            HashMap<String,String> addVariable = new HashMap<>();
            //Tipo de dato / Variable
            if(bandera){
                addVariable.put(variablesTipos.get(variable).split(",")[0],variable); //Añadimos la variable y su tipo de dato
            }else{
                addVariable.put(variablesTipos.get(lineaCodigo).split(",")[0],variable); //Añadimos la variable y su tipo de dato
            }
            addVariable.put("i", "0");
            addVariable.put("oldName", variable);
            variablesRepetidas.put(variable, addVariable);
            instanciarVariables(variable,bandera,lineaCodigo);
            if(bandera){
                setCodigoMain(lineaCodigo+";");
            }else{
                setAuxiliarFunciones(lineaCodigo+";");
            }
            
        }
        
    }
    
    public void instanciarVariables(String nameVariable,boolean bandera,String lineaCodigo){ //Escribe el tipo que es 
        
        //HasMap que nos ayudaran a recorrer los arreglos
        HashMap<String, HashMap<String, String>> variablesRepetidas = new HashMap<>();
        HashMap<String,String> variablesTipos = new HashMap<>();
        //Instanciamos los HasMap
        if (bandera) {//Si bandera es true estamos en el main
            variablesRepetidas = variableRepetida;
            variablesTipos = variablesTipo;
        } else {//Estamos en la funcion
            variablesRepetidas = variableRepetidaFunciones;
            variablesTipoFuncion.putAll(getDataFuniones(this.auxiliarNameFuncion));
            variablesTipos = variablesTipoFuncion;
        }
        
        String oldName = variablesRepetidas.get(nameVariable).get("oldName");
        String tipoDato = "",tipoDato2;
        if(bandera){
            tipoDato2 = variablesTipos.get(nameVariable).split(",")[Integer.valueOf(variablesRepetidas.get(nameVariable).get("i"))];
        }else{
            tipoDato2 = variablesTipos.get(lineaCodigo).split(",")[Integer.valueOf(variablesRepetidas.get(nameVariable).get("i"))];
        }
        
        for(String key: variablesRepetidas.get(nameVariable).keySet()){
            if(key.equals(tipoDato2)){
               tipoDato = variablesRepetidas.get(nameVariable).get(key);
               break;
            }
        }
        tipoDato2+=" ";
        if(tipoDato2.contains("-")){
            tipoDato2 = "vector<"+tipoDato2.substring(0,tipoDato2.indexOf("-"))+"> ";
        }
        if(bandera){
            instanciaVariable.add(tipoDato2+tipoDato+" ;");
        }else{
            funciones.add(1,tipoDato2+tipoDato+" ;");
        }
    }
    
    public String generarVariableUnica(String nameV){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,4);
    }
    
    public void instanciarFuncionesMain(String lineaCodigo){
        setCodigoMain(lineaCodigo+";");
    }
    
    public void generarFunciones(String nameFuncion){
        //Agregamos la funcion a la bandera 

        banderaFunciones.put(nameFuncion, 0);
        this.auxiliarNameFuncion = nameFuncion;
        this.space = 4;
        //Generamos la lista que contendra las funciones 
        this.funciones = new ArrayList<String>();

        //Obtenemos los datos necesarios
        HashMap<String,String> data = getDataFuniones(nameFuncion); //Con tipo de dato
        HashMap<String,String> dataCompleta = new HashMap<>();
        dataCompleta.putAll(getDataFunionesCompleta(nameFuncion)); //Sin tipo de dato pero se itera sobre el
        funciones.add(generarInstanciaFuncion(data.get(nameFuncion), data.get("parametros"),nameFuncion)); //Instancia la cabecera de la funcion
        String aux = "";
        //Eliminamos parametros de la dataCompleta
        dataCompleta.remove("name");
        dataCompleta.remove("parametros");
        //Leemos los datos
        for(String lineaCodigo: dataCompleta.values()){
            leerLineas(lineaCodigo, false);
        }
        funciones.add("}\n");
        instanciaFunciones.addAll(funciones);
        //Si la funcion esta sobrecargada 
        for(String sobrecargada: sobrecargaFunciones){
            if(sobrecargada.split(" ")[0].equals(nameFuncion)){
                funciones.remove(0);
                funciones.add(0, generarInstanciaFuncion(sobrecargada.split(" ")[3], data.get("parametros").split(" ")[0]+" "+sobrecargada.split(" ")[1],nameFuncion));
                this.instanciaFunciones.addAll(funciones);
            }
        }
        variableRepetidaFunciones.clear();
        variablesTipoFuncion.clear();
    }
    
    public HashMap<String,String> getDataFuniones(String nameFuncion){
        HashMap<String,String> data2 = new HashMap<>();
        for (HashMap<String, String> data : variablesTipoFunciones) {
            for (String key : data.keySet()) {
                if(key.equals(nameFuncion)){
                    data2.putAll(data);
                    return data2;
                }
            }
        }
        return null;
    }
    
    public HashMap<String,String> getDataFunionesCompleta(String nameFuncion){
        HashMap<String,String> data1 = new HashMap<>();
        HashMap<String,String> data2 = new HashMap<>();
        for (int i : funcionesAll.keySet()) {
            data1.putAll(funcionesAll.get(i));
            if(data1.get("name").equals(nameFuncion)){
                data2.putAll(data1); 
                return data2;
            }
            
        }
        return null;
    }
    
    public String generarInstanciaFuncion(String dataFuncion,String dataParametros,String nameFuncion){
        String lineaInstancia = "";
        if(dataFuncion.contains("-")){//Preguntamos si contiene un array
            lineaInstancia += "vector<"+dataFuncion.substring(0,dataFuncion.indexOf("-"))+">"; //Obtenemos la linea que que instancia la funcion 
        }else{
            lineaInstancia += dataFuncion;//Obtenemos la linea que que instancia la funcion
        }
        lineaInstancia+=" "+nameFuncion+"(";
        //Obtenemos los parametros
        String[] nameParametros = dataParametros.split(" ")[0].split(",");
        String[] dataParametro = dataParametros.split(" ")[1].split(",");
        for(int i = 0; i < dataParametro.length; i++){
            if(dataParametro[i].contains("-")){
                lineaInstancia += "vector<"+dataParametro[i].substring(0,dataParametro[i].indexOf("-"))+"> "+nameParametros[i]+"[],";
            }else{
                lineaInstancia += dataParametro[i]+" "+nameParametros[i]+",";
            }
        }
        if(lineaInstancia.endsWith(",")){
            lineaInstancia = lineaInstancia.substring(0,lineaInstancia.lastIndexOf(","));
        }
        lineaInstancia+="){";
        return lineaInstancia;
    }
    
    public void setInstanciarFor(String lineaCodigo,boolean bandera){
        String variableIteradora = lineaCodigo.substring(lineaCodigo.indexOf("r")+1,lineaCodigo.indexOf("in")).trim();
        String valorVariableIteradora = lineaCodigo.substring(lineaCodigo.indexOf("(")+1,lineaCodigo.indexOf(",")).trim();
        String array = lineaCodigo.substring(lineaCodigo.indexOf(",")+1,lineaCodigo.lastIndexOf(")")).trim();
        if(array.contains("len(")){
            array = array.substring(array.indexOf("(")+1,array.indexOf(")"))+".size()";
        }
        String conversor = "for(int "+variableIteradora+"="+valorVariableIteradora+";"+valorVariableIteradora+"<"+array+";"+variableIteradora+"++){";
        if(bandera){
            setCodigoMain(conversor);
        }else{
            String spacios = "";
            for(int i = 0; i< this.space-4; i++){
                spacios+=" ";
            }
            setAuxiliarFunciones(spacios+conversor);
        }
    }
    
    public int contadorEspacios(String linea) {
        int contador = 0;
        for (int i = 0; i < linea.length(); i++) {
            if (linea.charAt(i) == ' ') {
                contador++;
            } else {
                break;
            }
        }
        return contador;
    }
    
}
