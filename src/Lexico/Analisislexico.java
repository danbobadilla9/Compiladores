/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lexico;

import LoadFile.Archivo;
import Semantico.AnalisisSemantico;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author user
 */
public class Analisislexico {
    public int numeroFuncion = 1;
    public String[] tokens,data;
    public List<String> codigoMain = new ArrayList<String>(); //Nombre de las funciones 
    public Boolean itsFunction = false;
    
    public HashMap<Integer,HashMap> funcionesAll = new HashMap<Integer, HashMap>();
    
    public String[] getTokens(){
        int contadorEspacios = 0,espacioDefault = 4,contadorLinea=1;
        char[] caracteres;
        Archivo leerTxt = new Archivo("C:\\Users\\user\\Documents\\NetBeansProjects\\Compiladores\\src\\LoadFile\\CodigoPy.txt");
        //Obteniendo los datos en forma de Arreglo de String
        data = leerTxt.getData().stream().toArray(String[]::new);
        for(int i = 0; i<data.length; i++){
            contadorEspacios = 0;
            //Validamos los espacios 
            caracteres = data[i].toCharArray();//Convertimos la cadena en caracteres para analizar los espacios al inicio
                
            //Contamos los espacios en blanco del inicio
            for(char caracter: caracteres ){
                if(caracter != ' '){
                    break;
                }else{
                    contadorEspacios++;
                }
            }
            
            
            if(contadorEspacios == 0){ //Si es 0 podemos saber que termino la funcion
                
                if(contadorLinea == 1 && itsFunction){ //Si la funcion esta vacia mandamos error
                    mensajeErrorEspacio(i, "Función vacia ");
                    break;
                }
                
                itsFunction = false; 
                String exp = "^((print\\({1}\\s*(?=[a-zA-Z\"])\"{1}[a-zA-Z\\s]+\"{1}\\s*(?=\\))\\){1})|([a-zA-Z0-9]+\\s*(?==)=\\s*(?=[a-zA-Z])input\\s*(?=\\()\\({1}\\s*(?=\")\"{1}[a-zA-Z0-9\\s:]+\"{1}\\s*(?=\\))\\){1})|([a-zA-Z]+\\({1}\\s*((\"[a-zA-Z\\s:]+\"{1}\\s*(?=,),(?=[a-zA-Z\\s])\\s*(?=[a-zA-Z])[a-zA-Z]+\\s*\\){1})|(\\s*\"[a-zA-Z:\\s]+\"\\s*\\){1})|(\\s*[a-zA-Z]+\\s*\\){1})))|([a-zA-Z]+=\\s*[a-zA-Z]+\\({1}((\\s*int\\s*\\({1}\\s*[a-zA-Z]+\\s*\\){1}\\s*\\){1})|(\\s*[a-zA-Z]+)\\s*\\))))$";
                String exp2 ="^[a-zA-Z]+\\({1}\\s*(((([a-zA-Z]+\\s*,(?=[a-zA-Z\\s])\\s*)|[a-zA-Z]+\\s*)|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*,\\s*(?!,\\)))|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*)|)*)\\){1}$";
                String exp3 = "^[a-zA-Z]+\\s*=\\s*[a-zA-Z]+\\({1}\\s*(((([a-zA-Z0-9]+\\s*,(?=[a-zA-Z\\s])\\s*)|[a-zA-Z0-9]+\\s*)|([a-zA-Z0-9]+\\({1}\\s*[a-zA-Z0-9]+\\s*\\){1}\\s*(,(?![,\\)])\\s*(?![,\\)]))*)|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*,\\s*(?!,\\)))|(\"{1}[a-zA-Z\\s:]+\"{1}\\s*))*)\\){1}$";
                String exp4 = "^\\s{4}print\\s*\\({1}(((\\s*[a-zA-Z]+\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*\"{1}\\s*[a-zA-Z\\s]+\\s*\"{1}\\s*,\\s*(?=[a-zA-Z\"])))+)?((\\s*[a-zA-Z]+\\s*)|(\\s*\"{1}\\s*[a-zA-Z\\s]+\\s*\"{1}\\s*))\\){1}$";
                String exp5 = "^[a-zA-Z]+\\s*\\({1}(((\\s*[a-zA-Z]+\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*\"{1}\\s*[a-zA-Z:\\s]+\\s*\"{1}\\s*,\\s*(?=[a-zA-Z\"])))+)?((\\s*[a-zA-Z0-9]+\\s*)|(\\s*\"{1}\\s*[a-zA-Z:\\s]+\\s*\"{1}\\s*))\\){1}$";
                String exp6 = "^[a-zA-Z]+\\s*=\\s*(?!input)[a-zA-Z]+\\s*\\({1}(((\\s*[a-zA-Z]+\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*\"{1}\\s*[a-zA-Z:\\s]+\\s*\"{1}\\s*,\\s*(?=[a-zA-Z\"])))+)?((\\s*[a-zA-Z]+\\s*)|(\\s*\"{1}\\s*[a-zA-Z:\\s]+\\s*\"{1}\\s*))\\){1}$";
                String exp7 = "^([a-zA-Z0-9]+\\s*(?==)=\\s*(?=[a-zA-Z])input\\s*(?=\\()\\({1}\\s*(?=\")\"{1}[a-zA-Z0-9\\s:]+\"{1}\\s*(?=\\))\\){1})$";// lo puse porque no se donde esta la regex del input que permite mas variables
                String exp8 = "^[a-zA-Z0-9]+\\s*={1}\\s*(?!input)[a-zA-Z0-9]+\\s*\\({1}(((\\s*[a-zA-Z0-9]+\\s*\\({1}\\s*[a-zA-Z0-9]+\\s*\\){1}\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*[a-zA-Z0-9]+\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*\"{1}\\s*[a-zA-Z0-9\\s]+\\s*\"{1}\\s*,\\s*(?=[a-zA-Z\"])))+)?((\\s*[a-zA-Z0-9]+\\s*\\({1}\\s*[a-zA-Z0-9]+\\s*\\){1}\\s*)|(\\s*[a-zA-Z0-9]+\\s*)|(\\s*\"{1}\\s*[a-zA-Z0-9\\s]+\\s*\"{1}\\s*))\\){1}$";;//Funciones con numerales al inicio de una variable
                //Validamos que sea función
                if(Pattern.compile("^def\\s+[a-zA-Z]+\\s*\\({1}(\\s*(?=[a-zA-Z\\)])(([a-zA-Z0-9]+(\\s*,(?=[a-zA-Z0-9]))?|(\\s*,\\s*(?=[a-zA-Z]))?)+|[a-zA-Z0-9]+)?)\\s*\\){1}:{1}\\s*$").matcher(data[i]).matches()){
                    scanFuncion(data[i]);
                    itsFunction = true;
                }else if(Pattern.compile(exp8).matcher(data[i]).matches()||Pattern.compile(exp6).matcher(data[i]).matches()||Pattern.compile(exp5).matcher(data[i]).matches()|Pattern.compile(exp4).matcher(data[i]).matches()|Pattern.compile(exp).matcher(data[i]).matches() || Pattern.compile(exp2).matcher(data[i]).matches()|| Pattern.compile(exp3).matcher(data[i]).matches()){ //Si no es función se agrega al codigo MAIN
                    if(!Pattern.compile(exp7).matcher(data[i]).matches() && data[i].contains("input")){
                        mensajeErrorEspacio(i,"Error en el numero de parametros ");
                        System.exit(0);
                    }    
                    codigoMain.add(data[i]); //NECESITAMOS VALIDAR
                }else{
                    if(!data[i].isEmpty()){
                        System.out.println(data[i]);
                        mensajeErrorEspacio(i,"Error en la función");
                        System.exit(0);
                    }
                }
                contadorLinea = 1;
                
            }else if(contadorEspacios % 4 == 0){ //Obtenemos si son modulos de 4
                
                
                if(itsFunction){//Dentro de una funcion
                    //Valida print,creacion arreglos,ciclos for
                    String exp = "^((\\s{4}print\\s*\\({1}(\\s*(?=[a-zA-Z])(([a-zA-Z0-9]+(\\s*,(?=[a-zA-Z0-9]))?|(\\s*,\\s*(?=[a-zA-Z]))?)+|[a-zA-Z0-9]+)?)\\s*\\){1})||(\\s{4}[a-zA-z]+\\s*={1}\\s*\\[{1}[a-zA-Z]*\\]{1})||(\\s{4,8}for\\s+(?=[a-z])[a-zA-Z]\\s+(?=[a-z])in\\s+(?=[a-z])range\\({1}\\s*(?=[a-z])?[0-9]\\s*(?=[a-z])?,\\s*(?=[a-z])[a-zA-Z]+\\s*(?=[a-z\\)])\\){1}:{1}))$";
                    //Valida casteo, inputs,operacion vector suma
                    String exp2 = "^((\\s{4,8}[a-zA-Z]+\\s*(?=[a-z=])=\\s*(?=[a-z])((int)|(input))\\s*(?=[a-z\\(])\\({1}\\s*(?=[a-zA-Z])input\\s*(?=[\\(])\\({1}\\s*(?=[a-z\"])\"{1}[a-zA-Z\\s:]+\"{1}\\s*(?=[\\)])\\){1}\\s*(?=[\\)])\\){1})||(\\s{4,8}[a-zA-Z]+.append\\({1}\\s*[a-zA-Z]+\\s*\\){1}\\s*)||(\\s{4,8}return\\s*(?=[a-zA-Z])[a-zA-Z]+(?!\\S))|((\\s{4,8}[a-zA-Z]+\\s*(?==)=\\s*(?=[a-zA-Z])[a-zA-Z]+\\s*(?=[a-zA-Z\\+])\\+\\s*(?=[a-zA-Z\\+])[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}(?!\\S))))$";
                    String exp3 = "^((\\s{4,8}[a-zA-Z]+\\s*(?==)=\\s*(?=[a-zA-Z])[a-zA-Z]+\\[{1}[a-zA-Z0-9]\\]{1}[+*-][a-zA-Z]+)|(\\s{4,8}[a-zA-Z]+\\s*(?==)=\\s*(?=[a-zA-Z])[a-zA-Z]+\\[{1}\\s*(?=[a-zA-Z0-9\\]])[0-9]+\\s*(?=[a-zA-Z\\]])\\]{1}(?!\\S))|(\\s{4,8}for\\s*(?=[a-z])[a-zA-Z]\\s*(?=[a-z])in\\s*(?=[a-z])range\\({1}\\s*(?=[a-z])?[0-9]\\s*(?=[a-z])?,\\s*(?=[a-z])len\\({1}\\s*(?=[a-zA-Z])[a-zA-Z]+\\s*(?=\\))\\){2}:{1})|(\\s{4,8}[a-zA-Z]+\\s*(?==)=\\s*(?=[a-zA-Z\\+])[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}\\s*(?=[\\+\\*\\-])[+*-]\\s*(?=[a-zA-Z])[a-zA-Z]+)|(\\s{4,8}[a-zA-Z]+\\s*(?==)=[0-9]+))$";
                    //CAMBIE EN EXP3 9 POR 4,8 AL INICIO DE TODO 
                    String exp5 = "^\\s{4}print\\s*\\({1}(((\\s*[a-zA-Z]+\\s*,\\s*(?=[a-zA-Z\"]))|(\\s*\"{1}\\s*[a-zA-Z\\s]+\\s*\"{1}\\s*,\\s*(?=[a-zA-Z\"])))+)?((\\s*[a-zA-Z]+\\s*)|(\\s*\"{1}\\s*[a-zA-Z\\s]+\\s*\"{1}\\s*))\\){1}$";//Evalua el print correctamente ROBUSTO
                    //Evalua un return 
                    String exp4 = "^\\s{4,8}return\\s*(?=[a-zA-Z])\\w+$";
                    String exp4v2 = "^\\s{4,8}return\\s*(?=[a-zA-Z\"])\"{1}[a-zA-Z]+\"{1}$";
                    //Acepto un -> va = num_int
                    String exp6 = "^\\s{4,8}[a-zA-Z]+\\s*={1}\\s*[0-9]+$";
                    //Evalua operacion vector * int
                    String exp7 = "^\\s+[a-zA-Z]+\\s*={1}\\s*(([a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}\\*{1}[a-zA-Z]+)|([a-zA-Z]+\\*{1}[a-zA-Z]+\\[{1}[a-zA-Z]\\]{1}))$";
                    //valida -> varn = var
                    String exp8 ="^\\s{4,8}[a-zA-Z]+\\s*={1}\\s*[a-zA-Z0-9]+$";
                    if(Pattern.compile(exp4).matcher(data[i]).matches()||Pattern.compile(exp4v2).matcher(data[i]).matches()){//Evalua un return
                        itsFunction = false;
                    }
                    if(Pattern.compile(exp8).matcher(data[i]).matches()||Pattern.compile(exp7).matcher(data[i]).matches()||Pattern.compile(exp6).matcher(data[i]).matches()||Pattern.compile(exp5).matcher(data[i]).matches() ||Pattern.compile(exp4v2).matcher(data[i]).matches() ||Pattern.compile(exp).matcher(data[i]).matches() || Pattern.compile(exp2).matcher(data[i]).matches() || Pattern.compile(exp3).matcher(data[i]).matches()){
                        setLineas(contadorLinea,data[i]);
                        contadorLinea++;
                    }else{
                        mensajeErrorEspacio(i, "Error de codigo");
                    }
                    continue;
                }
                //Validamos que no sea un def con espacios al inicio
                if(Pattern.compile("^def\\s+.+$").matcher(data[i].trim()).matches()&& data[i].charAt(0) == ' '){
                    mensajeErrorEspacio(i, "Un def no puede estar indentado");
                    break;
                }
                //Si la linea de codigo no esta dentro de una funcion, manda error
                mensajeErrorEspacio(i, "La linea de codigo no esta dentro de una funcion");
                break;
            }else{ // Espacios en blanco que no son modulos de 4
                
                //Validamos que no sea un def con espacios al inicio
                if(data[i].charAt(0) == ' ' && Pattern.compile("^def\\s+.+$").matcher(data[i].trim()).matches()){
                    mensajeErrorEspacio(i, "Un def no puede estar indentado");
                }else{ //Validacion Error de identacion
                    mensajeErrorEspacio(i,"Error de identación");
                    break;
                }
                
            }

        }
//        mostrarFunciones();
        //Pasamos al analisis semantico
        AnalisisSemantico semantica = new AnalisisSemantico(funcionesAll,codigoMain);
        return tokens;
    }
    
    //generamos un arreglo con todas las funciones 
    public void scanFuncion(String cadena){
//        System.out.println(" DATA \n");
        cadena = cadena.replace(" ",""); // Quitamos espacios en blanco 
        
        HashMap<String,String> codigoFuncion = new HashMap<String, String>();
        codigoFuncion.put("name",cadena.substring(3, cadena.indexOf("(")));//Obtenemos El nombre de las funciones
        codigoFuncion.put("parametros",cadena.substring(cadena.indexOf("(")+1, cadena.indexOf(")")));//Obtenemos nombre de los parametros 
        funcionesAll.put(this.numeroFuncion,codigoFuncion);
        this.numeroFuncion++;
    }
    
    public void setLineas(int contadorLinea,String data){
        HashMap<String,String> funciones = funcionesAll.get(this.numeroFuncion-1);
        funciones.put(String.valueOf(contadorLinea), data);
    }
    
    public void mostrarFunciones(){
        for (HashMap<String, String> funcion : funcionesAll.values()) {
            System.out.println(funcion);
            System.out.println("\n");
            for (String datos : funcion.values()) {
                System.out.println(datos);
            }
        }
//        codigoMain.forEach(datos -> System.out.println(datos));
    }
    
    
    public void mensajeErrorEspacio(int linea, String mensaje){
        System.out.println(mensaje + ":\n En la linea " + linea);
        System.exit(0);
    }
    
    
    
    
}
