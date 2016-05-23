package server;

import java.io.*;
import java.net.*;
import java.util.*;

import bozo.*;

/**
 * Classe que representa um servidor
 * @author Lucas Alexandre Soares - 9293265
 * @author Rafael Augusto Monteiro - 9293095
 */
public class Server {
	//marca quando o servidor deve estar fechado ou aberto
	private boolean flag;
	//ServerSocket utilizado no servidor
	private ServerSocket ss;
	/*TreeMap de jogadores
	Armazena o id e a pontuacao de um jogador*/
	private SortedMap<String,Player> players;
        
    /**
     * Construtor padrao da classe
     */
    public Server(){
		this.flag = true;
		this.players = Collections.synchronizedSortedMap(new TreeMap());
		ss = null;
	}

	// setter para a flag que diz se o servidor deve estar ligado ou nao
	private synchronized void setFlag(){
		this.flag = false;
	}
	
	/**
	 * Metodo que inicia o servidor
	 * @param port porta utilizada pelo servidor
	 * @throws IOException 
	 */
	public void startServer(int port) throws IOException{

            int nclients = 0;
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
                try {
                    client = ss.accept();
                    nclients++;
                } catch(Exception e){
                    System.out.println("Connection error: " + e);
                    return;
                }

                //disparo uma thread do servidor (que joga bozo com o cliente)
                ServerThread st = new ServerThread(client, this);
                st.start();
            }
                
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException ex) {}
            
            System.out.println("Top Player: " + this.players.get(this.players.lastKey()).getId());
            System.out.println("Score: " + this.players.get(this.players.lastKey()).getScore());
            System.out.println("Play count: " + this.players.get(this.players.lastKey()).getPC());
            System.out.println("Average score: " + (this.players.get(this.players.lastKey()).getScore()/this.players.get(this.players.lastKey()).getPC()));
	}
	
	/**
	 * Classe que contem o thread utilizado no servidor
	 * esse thread realiza uma partida de bozo com um cliente
	 * 
	 */
	public class ServerThread extends Thread {
		
		private Socket skt;
		private Server s;
		
		private Scanner serverIn;
		private PrintStream serverOut;
		
		private String id;
		private RolaDados rd;
		private Placar p;
		
		//construtor do thread
		private ServerThread(Socket skt, Server s){
                    this.skt = skt;
                    this.s = s;
                    this.id = null;
                    this.rd = new RolaDados(5);
                    this.p = new Placar();

                    //abrindo streams para se comunicar com o cliente
                    try{
                            this.serverIn = new Scanner(skt.getInputStream());
                            this.serverOut = new PrintStream(skt.getOutputStream());
                    } catch(Exception e){}
		}
		
		@Override
		public void run(){

                    String msg = null;
                    String response = null;

                    /*try-catch para evitar nullpointerexception caso algo seja finalizado
                    no meio da execucao de uma thread
                    achei melhor q esperar as threads acabarem
                    */
                    try{
                        do {
                            msg = serverIn.nextLine();
                            if(this.id == null){
                                System.out.println("New client @ "+ skt.getInetAddress() + " says " + msg);   
                            }

                            String[] args = msg.split(" ");
                            //decodifico a mensagem lida
                            if(args.length == 1) { //F e R<n>
                                if (args[0].equals("F")) { //F

                                    //finaliza partida
                                    response = "\nFim de jogo\nPontuacao: " + p.getScore();
                                    serverOut.println(response);

                                    //gero um registro Player novo e insiro na lista de jogadores
                                    Player plr = null;
                                    if(s.players.get(this.id) == null) {
                                        //insiro um novo player
                                        plr = new Player(this.id, p.getScore());
                                        s.players.put(this.id, plr);
                                    } else {
                                        //atualizo o score do player existente 
                                        s.players.get(this.id).addScore(p.getScore());
                                        s.players.get(this.id).incPC();
                                    }
                                    //finalizo a comunicacao com o cliente
                                    System.out.println(this.id + " scored " + p.getScore()); 
                                    endComm();
                                    break;
                                } else { //R<n>
                                    //chama a proxima rodada
                                    rd.rolar();
                                    response = rd.getStringDados();
                                    serverOut.println(response);
                                }

                        } else if(args.length == 2){ //I <id> e P<n> <pos>

                            if(args[0].equals("I")){ //I <id>

                                //Altero o id da thread
                                id = args[1];
                                response = "Bem vindo!";
                                serverOut.println(response);

                            } else { //P<n> <pos>
                                //Coloco os pontos no placar
                                p.add(Integer.parseInt(args[1]), rd.getDados());
                                response = "" + p.getScore();
                                serverOut.println(response);
                            }

                        } else { //T <d1> <d2> <d3> <d4> <d5>
                            //vetor com os dados a serem rolados

                            boolean[] quais = new boolean[5];

                            //preencho um vetor com os dados a serem rolados
                            for(int i = 1; i < 6; i++){
                                    quais[i-1] = args[i].equals("1");
                            }   //rolo os dados
                            rd.rolar(quais);
                            response = rd.getStringDados();
                            serverOut.println(response);
                        }

                        } while(serverIn.hasNextLine());
                    }catch(Exception e){}
		}

		private void endComm() {

                    //fecho a conexao com o cliente
                    if(!flag){
                            serverOut.println("Total Score: " + s.players.get(this.id).getScore());
                            serverOut.println("Play Count: " + s.players.get(this.id).getPC());
                            serverOut.println("Avg. Score: " + (s.players.get(this.id).getScore()/s.players.get(this.id).getPC()));
                            serverOut.println("Bye");
                    }
                    try {
                            skt.close();
                    } catch (IOException ex) {}
                        
		}
	}
	
	/**
	 * Classe que contem o thread utilizado para leitura em paralelo da stdin
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
            private String id;
            private Long score;
            private Long playCount;

            public Player(String id, long score){
                    this.id = id;
                    this.score = score;
                    this.playCount = 0l;
            }

            public long getScore() { return this.score; }
            public String getId() { return this.id; }
            public long getPC() { return this.playCount; }

            public void addScore(long score) { this.score += score; }
            public void setId(String id) { this.id = id; }

            public void incPC() { this.playCount++; }

            @Override
            public int compareTo(Player other) {
                    if(this.id.equals(other.id)) return 0;
                    if(this.score > other.getScore()) return 1;
                    else return -1;
            }
	}

	/**
	 * Funcao main
	 * @param args the command line arguments
     * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception{
		Server s = new Server();
		s.startServer(9669);
	}
}
