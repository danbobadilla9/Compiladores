package LoadFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jdk.nashorn.internal.parser.TokenType;

public class Archivo {
    public String nameFile = "";
    public List<String> data = new ArrayList<String>();
    public boolean bandera = false;
    public boolean banderaFuncionesSaltos = false;
    public String auxLinea = "";
    public Archivo(String nameFile){
        this.nameFile = nameFile;
    }
    
    public List<String> getData() {
        File txt = new File(this.nameFile);
        try{
           BufferedReader obj = new BufferedReader(new FileReader(txt));
           String st = "";
           while((st = obj.readLine()) != null){
               data.add(analisisBasisco(cleanStringLastIndex(st)));
           }
        }catch(FileNotFoundException e){
            System.out.println("No se Encontro el archivo: "+this.nameFile+" "+e);
        } catch (IOException ex) {
            System.out.println("Error en la lectura del archivo "+ex);
        }
        return data;
    }
    
    public String cleanStringLastIndex(String linea){
        String newLine = "";
        while(linea.endsWith(" ")){
            linea = linea.substring(0,linea.length()-1);
        }
        newLine = linea;
        return newLine;
    }
    
    public String analisisBasisco(String linea){
        String exp1 = "^(cin|cout|for)\\s*={1}.+$"; //Analisa que no se tenga asignacion de variables con el nombre cout y cin
        String exp2 = "^([0-9]+)\\s*={1}.+$"; //Analisa que no sean puros numeros 
        String exp3 = "^([0-9]{1}(?=[a-zA-Z])[a-zA-Z0-9]+)\\s*={1}.+$"; //Analisa que no comience con numeros 
        String exp4 = "^[a-zA-Z0-9\\@]+\\s*={1}.+$"; //Evaluamos que no contenga un operador en la creacion de la variable
        String exp5 = "^#.+$"; // Evalua los comentarios #
        String exp6 = "^\"\"\"$";// Evalua los comentarios tipo """
        
        //Evaluacion de creacion de funciones con saltos de linea 
        String exp7 = "^def\\s*[a-zA-Z]+\\({1}(\\n*)?([a-zA-Z,]+)?\\)?\\n?$";
        String exp8 = "^([a-zA-Z]+)?(\\){1})?:{1}$";
        
        //Evaluacion de creacion de funciones con saltos de linea 
        String exp9 = "^\\s*print\\s*\\({1}(\\n*)?([a-zA-Z,]+)?\\)?\\n?$";
        String exp10 = "^\\s*[a-zA-Z]+\\s*\\){1}(?!:)$";
        if(Pattern.compile(exp1).matcher(linea).matches()){
            errorMensaje("Error! No se puede crear una variable con una palabra reservada de c++ \nLinea: "+linea);
        }
        
        if(Pattern.compile(exp2).matcher(linea).matches()){
            errorMensaje("Error! Una variable no puede contener solo numeros \nLinea: "+linea);
        }
        
        if(Pattern.compile(exp3).matcher(linea).matches()){
            errorMensaje("Error! Una variable no puede iniciar con un numero \nLinea: "+linea);
        }
        
        if(Pattern.compile(exp4).matcher(linea).matches()){
            String aux = linea.substring(0,linea.indexOf("="));
            if( aux.contains("@") ||aux.contains("*")|| aux.contains("/")|| aux.contains("-") || aux.contains("+")|| aux.contains("%") ){
                errorMensaje("Error! Una variable no puede contener caracteres especiales");
            }
        }
        
        if(Pattern.compile(exp5).matcher(linea).matches()){
            linea = "";
        }
        if(Pattern.compile(exp6).matcher(linea).matches()){
            this.bandera = !this.bandera;
            linea = "";
        }
        
        if(this.bandera){
            linea = "";
        }
        if (this.banderaFuncionesSaltos) {
            if (Pattern.compile(exp8).matcher(linea).matches()) {
                auxLinea += linea;
                linea = auxLinea;
                auxLinea= "";
            }
            if(Pattern.compile(exp10).matcher(linea).matches()){
                auxLinea += linea;
                linea = auxLinea;
                auxLinea= "";
            }
            this.banderaFuncionesSaltos = false;
        }
        if(Pattern.compile(exp7).matcher(linea).matches()){
            this.banderaFuncionesSaltos = true;
            auxLinea = linea;
            linea = "";
        }
        
        if(Pattern.compile(exp9).matcher(linea).matches()){
            this.banderaFuncionesSaltos = true;
            auxLinea = linea;
            linea = "";
        }
        
        return linea; 
    }
    
    public void errorMensaje(String mensaje){
        System.out.println(mensaje);
        System.exit(0);
    }
}
