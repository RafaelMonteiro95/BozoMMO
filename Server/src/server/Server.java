package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import bozo.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 *
 * @author Rafael Augusto Monteiro - 9293095
 */
public class Server {
    //marca quando o servidor deve estar fechado ou aberto
    private boolean flag;
    //ServerSocket utilizado no servidor
    private ServerSocket ss;
    /*TreeMap de jogadores
    Armazena o ID e a pontuacao de um jogador*/
    private SortedMap<String,Player> players;
    
    
    
    public Server(){
        this.flag = true;
        this.players = Collections.synchronizedSortedMap(new TreeMap());
        ss = null;
    }
    
    /**
     * setter para a flag que diz se o servidor deve estar ligado ou nao
     */
    public synchronized void setFlag(){
        this.flag = false;
    }
    
    /**
     * Metodo que inicia o servidor
     * @param port porta utilizada pelo servidor
     * @throws IOException 
     */
    public void startServer(int port) throws IOException{
        //ouvindo a porta
        ss = new ServerSocket(port);
        System.out.println("Conectado a porta " + port);
        //crio uma thread para ouvir inputs da stdin
        InputThread inThread = new InputThread(this);
        inThread.start();
        
        //crio threads enquanto a inThread nao desligar a flag
        while(this.flag){
            //crio um socket novo
            Socket client = null;
            //espero ateh alguem se conectar ao ServerSocket
            try {client = ss.accept();}catch(Exception e){}
            //disparo uma thread do servidor (que joga bozo com o cliente)
            ServerThread st = new ServerThread(client, this);
            st.start();
        }
        
        if(!this.flag){
            System.out.println("Top Player: " + this.players.get(this.players.lastKey()).getId());
            System.out.println("Score: " + this.players.get(this.players.lastKey()).getScore());
            System.out.println("Play count: " + this.players.get(this.players.lastKey()).getPC());
        }
    }
    
    /**
     * Classe que contém o thread utilizado no servidor
     * esse thread realiza uma partida de bozo com um cliente
     * 
     */
    public class ServerThread extends Thread {
        
        private Socket skt;
        private Server s;
        
        private Scanner clientIn;
        private PrintStream clientOut;
        
        private String ID;
        private RolaDados rd;
        private Placar p;
        
        //construtor do thread
        private ServerThread(Socket skt, Server s){
            this.skt = skt;
            this.s = s;
            this.ID = null;
            this.rd = new RolaDados(5);
            this.p = new Placar();
            
            //abrindo streams para se comunicar com o cliente
            try{
                this.clientIn = new Scanner(skt.getInputStream());
                this.clientOut = new PrintStream(skt.getOutputStream());
            } catch(Exception e){}
            
        }
        
        @Override
        public void run(){
            
            String msg = null;
            
            /*try-catch para evitar nullpointerexception caso algo seja finalizado
            no meio da execucao de uma thread
            achei melhor q esperar as threads acabarem
            */
            try{
                do {
                    msg = clientIn.nextLine();
                    String[] args = msg.split(" ");
                    //decodifico a mensagem lida
                    if(args.length == 1) { //F e R<n>
                        if (args[0].equals("F")) { //F
                            //finaliza partida
                            clientOut.println("\nFim de jogo");
                            clientOut.println("Pontuacao: "+p.getScore());
                            //gero um registro Player novo e insiro na lista de jogadores
                            Player plr = null;
                            if(s.players.get(this.ID) == null) {
                                //insiro um novo player
                                plr = new Player(this.ID, p.getScore());
                                s.players.put(this.ID, plr);
                            } else {
                                //atualizo o score do player existente 
                                s.players.get(this.ID).addScore(p.getScore());
                                s.players.get(this.ID).incPC();
                            }
                            //finalizo a comunicacao com o cliente
                            endComm();
                            break;
                        } else { //R<n>
                            //chama a proxima rodada
                            clientOut.println(Arrays.toString(rd.rolar()));
                        }
                    } else if(args.length == 2){ //I <ID> e P<n> <pos>
                        if(args[0].equals("I")){ //I <ID>
                            //Altero o ID da thread
                            ID = args[1];
                            clientOut.println("Bem vindo!");
                        } else { //P<n> <pos>
                            //Coloco os pontos no placar
                            p.add(Integer.parseInt(args[1]), rd.getDados());
                            clientOut.println(p.getScore());
                        }
                    } else { //T <d1> <d2> <d3> <d4> <d5>
                        //vetor com os dados a serem rolados
                        boolean[] quais = new boolean[5];
                        //preencho um vetor com os dados a serem rolados
                        for(int i = 1; i < 6; i++){
                            quais[i-1] = args[i].equals("1");
                        }   //rolo os dados
                        clientOut.println(Arrays.toString(rd.rolar(quais)));
                    }
                } while(clientIn.hasNextLine());
            }catch(Exception e){}
        }

        public void endComm() {
            //fecho a conexao com o cliente
            if(!flag){
                clientOut.println("Total Score: " + s.players.get(this.ID).getScore());
                clientOut.println("Play Count: " + s.players.get(this.ID).getPC());
                clientOut.println("Bye bye!");
            }
            try {
                skt.close();
            } catch (IOException ex) {}
        }
    }
    
    /**
     * Classe que contém o thread utilizado para leitura em paralelo da stdin
     * esse thread verifica se o servidor deve ser fechado
     * a entrada para fechar o servidor deve ser "exit"
     */
    private class InputThread extends Thread{
        //guarda o endereco do servidor para fecha-lo
        Server s;
        
        InputThread(Server s){
            this.s = s;
        }
        
        @Override
        public void run(){
            
            String msg = null;
            //leio mensagens da stdin
            Scanner input = new Scanner(System.in);
            do{
                //quando ler "exit" seto a flag para fechar o programa
                if(input.nextLine().equals("exit")){
                    s.setFlag();
                    break;
                }
            } while(true);
            
            //fecho o ServerSocket(para interromper o ss.accept e sair de um loop infinito)
            try {
                s.ss.close();
            } catch (IOException ex) {}
        }
    }
    
    /**
     * classe que encapsula jogadores
     */
    private class Player implements Comparable<Player>{
        private String ID;
        private Long score;
        private Long playCount;
        
        public Player(String ID, long score){
            this.ID = ID;
            this.score = score;
            this.playCount = 0l;
        }
        
        public long getScore(){return this.score;}
        public String getId(){return this.ID;}
        public long getPC(){return this.playCount;}
        
        public void addScore(long score){this.score += score;}
        public void setId(String ID){this.ID = ID;}
        
        public void incPC(){this.playCount++;}

        @Override
        public int compareTo(Player o) {
            if(this.ID.equals(o.ID)) return 0;
            if(this.score > o.getScore()) return 1;
            else return -1;
        }
        
        
    }

    /**
     * Funcao main
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        Server s = new Server();
        s.startServer(80);
    }

}
