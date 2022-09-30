package Traductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Interprete {
    
    public HashMap<Integer,HashMap> funcionesAll = new HashMap<Integer, HashMap>(); //Contiene las funciones completas originales 
    public HashMap<String,String> variablesTipo = new HashMap<String,String>(); //Contiene las variables instanciadas del main y su tipo de dato
    public List<HashMap<String,String>> variablesTipoFunciones = new ArrayList<HashMap<String,String>>(); //Contiene las funciones completas pero ya su tipo de dato
    public List<String> sobrecargaFunciones = new ArrayList<String>(); //Contiene las funciones que son sobrecargadas 
    public String[] codigoMain; //Codigo del main
    public String[][] funcionesReservadas = new String[][]{{"print","Void"},{"input","string"},{"int","int"}}; //Funciones reservadas
    
    //Formando el codigo traducido
    public List<String> encabezadoLibreria = new ArrayList<String>(); //Librerias de C++
    public List<String> codigoPrincipal = new ArrayList<String>(); //Librerias de C++
            
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
        leerMain();
        //Agregamos el final del cierre de la clase int main()
        setCodigoMain("return 0;");
        setCodigoMain("\n}");
        mostrarData();
    }
    
    public void setLibrerias(String libreria){
        encabezadoLibreria.add(libreria);
    }
    public void setCodigoMain(String codigo){
        codigoPrincipal.add(codigo);
    }
        
    public void leerMain(){
        //Expresiones regulares que nos ayudaran a guiarnos en que parte estamos 
        String exp1 = "^\\s*print\\s*.+$";
        for(String linea: codigoMain){
            if(Pattern.compile(exp1).matcher(linea).matches()){
                generarCount(linea);
            }
        }
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
    public void generarCount(String linea){
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
            aux += " << \\n";
            setCodigoMain(aux);
        }else{
            setCodigoMain("count << "+linea+" << \\n;");
        }
    }
}
