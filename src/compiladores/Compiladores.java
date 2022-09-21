/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladores;

import Lexico.Analisislexico;

/**
 *
 * @author user
 */
public class Compiladores {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Analisislexico init = new Analisislexico();
        init.getTokens();
    }
    
}
