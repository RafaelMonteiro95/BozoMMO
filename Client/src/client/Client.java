package client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Rafael Augusto Monteiro - 9293095
 */
public class Client {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        boolean flag = true; //marca se devo continuar me comunicando
        String response = null;
        Socket s = null;
        
        while(flag){
            
            //tento me conectar ao servidor
            try{
                s = new Socket("127.0.0.1", 80);
            }catch (Exception e){
                System.out.println("Não foi possivel conectar ao servidor");
                System.exit(0);
            }
            
            //streams de entrada
            Scanner serverIn = new Scanner(s.getInputStream());
            PrintStream serverOut = new PrintStream(s.getOutputStream());

            //usada para guardar as msgs recebidas do servidor
            String msg = null;
            //envio a mensagem inicial
            serverOut.println("I 92930");
            if(serverIn.hasNextLine())serverIn.nextLine();

            //jogo por 10 rodadas
            for(int i = 0; i < 10; i++){
                //mensagem de rodada
                serverOut.println("R"+(i+1));

                //leio os dados
                msg = serverIn.nextLine();
                //Escolho os dados a serem mudados
                msg = "0 0 0 0 0";
                serverOut.println("T " + msg);

                //leio os dados
                msg = serverIn.nextLine();
                //Escolho os dados a serem mudados
                msg = "0 0 0 0 0";
                serverOut.println("T " + msg);

                //leio os dados
                msg = serverIn.nextLine();
                //Escolho os dados a serem mudados
                msg = "0 0 0 0 0";
                serverOut.println("T " + msg);
                //leio os dados
                msg = serverIn.nextLine();

                //Escolho a posicao do placar
                serverOut.println("P"+(i+1) + " " + (i+1));
                //Leio o score
                msg = serverIn.nextLine();

            }

            //mensagem que finaliza a execucao
            serverOut.println("F");

            //leio mensagem do servidor
            while (serverIn.hasNextLine()) {
               response = serverIn.nextLine();
               System.out.println(response);
               //se a resposta for "Bye Bye", paro de me comunicar
               if(response.equals("Bye bye!")){
                   flag = false;
               }
            }
            //abracos, dela
        }
    }
}
