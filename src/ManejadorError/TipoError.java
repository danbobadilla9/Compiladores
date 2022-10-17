package ManejadorError;

public class TipoError {
    public String linea = "";
    
    public TipoError(String linea){
        this.linea = linea;
    }
    
    
    public void getTipoError(){
        contadorParentesis();
        contadorComas();
    }
    
    public void contadorParentesis(){
        int parentesisA = 0,parentesisC = 0;
        int corcheteA = 0,corcheteC = 0;
        for(int i = 0; i < this.linea.length(); i++){
            char  letra = linea.charAt(i);
            if(letra == '('){
                parentesisA++;
            }else if(letra == ')'){
                parentesisC++;
            }else if( letra == '[' ){
                corcheteA++;    
            }else if( letra == ']'){
                corcheteC++;
            }
        }
        if(parentesisA != parentesisC){
            System.out.println("Error en los parentesis");
        }else if(corcheteA != corcheteC){
            System.out.println("Error en los parentesis");
        }
    }
    
    public void contadorComas(){
        int comas = 0;
        boolean bandera = false;
        for (int i = 0; i < this.linea.length(); i++) {
            char letra = linea.charAt(i);
            if(letra == ' '){
                continue;
            }
            if(bandera){
                if(letra !=  ')' && letra != ',' && letra != ':' ){

                    bandera = false;
                }
            }
            if(letra == ','){
                bandera = true;
            }
            
        }
        if(bandera){
            System.out.println("Error en los parametros de la funcion");
        }
    }
    
}
