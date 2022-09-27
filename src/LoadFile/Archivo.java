package LoadFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.parser.TokenType;

public class Archivo {
    public String nameFile = "";
    public List<String> data = new ArrayList<String>();
    public Archivo(String nameFile){
        this.nameFile = nameFile;
    }
    
    public List<String> getData() {
        File txt = new File(this.nameFile);
        try{
           BufferedReader obj = new BufferedReader(new FileReader(txt));
           String st = "";
           while((st = obj.readLine()) != null){
               data.add(cleanStringLastIndex(st));
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
}
