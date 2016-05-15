package bozo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Realiza leitura da entrada padrão
 * @author Lucas Soares - 9293265
 */
public class EntradaTeclado {
    
    static InputStreamReader isr = new InputStreamReader(System.in);
    static BufferedReader br = new BufferedReader(isr);

    /**
     * Le uma string da entrada padrao
     * @return uma string lida
     * @throws IOException IOException
     */
    public static String readString() throws IOException {
        
        String x;
        x = br.readLine();
        
        return x; 
    }

    /**
     * le um unsigned int
     * @return inteiro maior ou igual a 0
     */
    public static int readUInt() {

        boolean valid = false;
        int x = 0;

        while(!valid){

            try {
                x = leInt();
                valid = true;

            } catch (Exception e){
                valid = false;
            }

            if(!valid || x < 0) valid = false;
            else valid = true;
        }
        return x;
    }

    /**
     * le um inteiro
     * @return um inteiro lido
     */
    public static int readInt() {

        boolean valid = false;
        int x = 0;

        while(!valid){

            try {
                x = leInt();
                valid = true;

            } catch (Exception e){
                valid = false;
            }
        }
        return x;
    }



    private static int leInt() throws IOException {
        
        String x = readString();
        
        return Integer.parseInt(x);
    }
}