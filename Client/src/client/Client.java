package client;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;

/**
 * @authos Lucas Alexandre Soares - 9293265
 * @author Rafael Augusto Monteiro - 9293095
 */
public class Client {
	
	private static final int OCCUPIED = -1;
	private static final int NOTHING = 0;
	private static final int FULL_HAND = 6;
	private static final int STRAIGHT = 7;
	private static final int FOUR_OF_A_KIND = 8;
	private static final int FIVE_OF_A_KIND = 9;

	
	/**
	 * @param args the command line arguments
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws IOException {

		// System control variables
		Scanner sc = new Scanner(System.in);
		boolean flag = true; //marca se devo continuar me comunicando

		// Game variables
		int pos;
		String score;
		boolean[] playedPos = new boolean[10];
		String reroll;
		String canonicalMsg;

		// Comunication variables
		String ip;
		int port;
		String response = null;
		Socket s = null;

		// DEBUG
		ip = "127.0.0.1";
		port = 80;
		// System.out.println("Entre com o ip do servidor: ");
		// ip = sc.nextLine();
		// System.out.println("Entre com a porta de comunicacao: ");
		// port = sc.nextInt();


		while(flag){
			
			//tento me conectar ao servidor
			try{
				s = new Socket(ip, port);
			} catch (Exception e){
				System.out.println("Não foi possivel conectar ao servidor:");
				System.out.println("\tip: " + ip);
				System.out.println("\tport: " + port);
				System.out.println("Error: " + e);
				System.out.println("Saindo...");
				System.exit(1);
			}
		
			System.out.println("Conectado no servidor!");
			System.out.println("ip: " + ip);
			System.out.println("port: " + port);
			
			//streams de entrada
			Scanner clientIn = new Scanner(s.getInputStream());
			PrintStream clientOut = new PrintStream(s.getOutputStream());

			//usada para guardar as msgs recebidas do servidor
			String msg = null;
			//envio a mensagem inicial
			response = "I 9392095";
			clientOut.println(response);
			// DEBUG
			System.out.println("SENDING '" + response + "'");

			String dbg_msg = null;
			if(clientIn.hasNextLine()) {
				try{
					dbg_msg = clientIn.nextLine();
				} catch (Exception e){
					System.out.println("Connection closed");
					System.out.println("Error message: " + e);
					break;
				}
				
				// DEBUG
				System.out.println("======================");
				System.out.println("Server msg: " + dbg_msg);
				System.out.println("======================");
			}

			// Initialize play positions
			Arrays.fill(playedPos, false);
			pos = 0;

			// 10 rounds
			for(int i = 0; i < 10; i++){
				
				// DEBUG
				System.out.println("PRESS ENTER TO PLAY NEXT ROUND");
//				sc.nextLine();

				/* Call next round (R <round number>) */
				response = "R" + (i+1);
				clientOut.println(response);
				// DEBUG
				System.out.println("SENDING '" + response + "'");

				/* Parse dice vector 
				input from server:	[1, 2, 3, 4, 5] 
				function output:		1 2 3 4 5
				*/
				msg = clientIn.nextLine();
				// DEBUG
				System.out.println("RECEIVED '" + msg + "'");
				canonicalMsg = msg;

				/* Reroll dice (T <dice vector>) [reroll twice]
				input:		1 2 3 4 5
				function: 	0 0 1 0 0 (send to server)
				output:		1 2 2 4 5
				*/

				// format converts boolean 'true' and 'false' strings 
				// to 1 or 0 characters respectively
				reroll = format(rerollDice(canonicalMsg, playedPos));
				response = "T " + reroll;
				clientOut.println(response);
				// DEBUG
				System.out.println("SENDING '" + response + "'");

				msg = clientIn.nextLine();
				// DEBUG
				System.out.println("RECEIVED '" + msg + "'");
				canonicalMsg = msg;
				reroll = format(rerollDice(canonicalMsg, playedPos));
				response = "T " + reroll;
				clientOut.println(response);
				// DEBUG
				System.out.println("SENDING '" + response + "'");
				
				
				/* Find score position (P <n> <pos>)
				pos in [1, 10]
				pos = 3 (send to server)
				*/
				msg = clientIn.nextLine();
				// DEBUG
				System.out.println("RECEIVED '" + msg + "'");

				canonicalMsg = msg;
				pos = calculatePosition(playedPos, canonicalMsg);
                                if(pos < 0) pos = findUnnusedPos(playedPos);
//				printvector(playedPos, "playedPos");
				response = "P"+(i+1) + " " + (pos+1);
				clientOut.println(response);
				playedPos[pos] = true;	// Update played positions
				// DEBUG
				System.out.println("SENDING '" + response + "'");

				score = clientIn.nextLine();
				System.out.println("Scored " + score + " points");
			}

			/* Finalize connection (F) */
			response = "F";
			clientOut.println(response);
			// DEBUG
			System.out.println("SENDING '" + response + "'");
			

			//leio mensagem do servidor
			while (clientIn.hasNextLine()) {

				try{
					response = clientIn.nextLine();
					// DEBUG
					System.out.println("RECEIVED '" + response + "'");
				} catch (Exception e){
					System.out.println("Connection closed");
					System.out.println("Error message: " + e);
					flag = false;
					break;
				}

				System.out.println(response);
				
				//se a resposta for "Bye", paro de me comunicar
				if(response.equals("Bye"))
					flag = false;
			}
		}
	}
        
        private static int findUnnusedPos(boolean[] playedPos){
            int i = 0;
            for(i = 0; i < 10; i++) if(!playedPos[i])break; 
            return i;
        }

	private static String canonicalize(String msg){
		msg = msg.replace(",", "");
		msg = msg.replace("[", "");
		msg = msg.replace("]", "");
		return msg;
	}

	private static int[] canonicalRepToInt(String canonicalMsg){
		
		int[] dice = new int[5];

		// Convert canonical message to integer array
		for (int i = 0; i < 5; i++)
			dice[i] = canonicalMsg.charAt(2*i) - '0';

		return dice;
	}

	// DEBUG
	private static void printvector(int[] v, String name){
		
		System.out.printf("\t[DEBUG] %s[]:", name);
		for (int i = 0; i < v.length; i++) System.out.printf(" %d", v[i]);
		System.out.println("");
	}
	// DEBUG
	private static void printvector(boolean[] v, String name){
		
		System.out.printf("\t[DEBUG] %s[]:", name);
		for (int i = 0; i < v.length; i++) System.out.printf(" %b", v[i]);
		System.out.println("");
	}

	private static boolean[] rerollDice(String canonicalMsg, boolean[] playedPos){

		boolean[] reroll = new boolean[5];	// Initialized to false
		boolean[] unsortedReroll = new boolean[5];

		int[] dice = canonicalRepToInt(canonicalMsg);
		int[] bkp = Arrays.copyOf(dice, dice.length);

		int special;
		int i = 0;
		int value;

		Arrays.sort(dice);
		special = checkHand(dice, playedPos);

		// If already has special hand that has not been
		// played yet then don't reroll any dice
		if(special == NOTHING){

			// Calculate biggest repetition
			int biggest = 0;
			int bPos = 0, discardIndex = 0;
			boolean twoGroups = false;
			int[] repetitions = new int[6];

			// Calculate repetitions
			value = dice[i];
			for (i = 0; i < 5; i++) {
				if(value == dice[i]) repetitions[value-1]++;
				else {
					value = dice[i];
					repetitions[value-1]++;
				}
			}

			// Find biggest
			for(i = 0; i < 6; i++) {
				if(repetitions[i] >= biggest){
					biggest = repetitions[i];
					bPos = i;
				}
			}
			value = bPos + 1;

			// Separate in cases based on most recurring value
			switch(biggest){
			case 1:
				// If straight has not been played, try it
				if(!playedPos[STRAIGHT]) reroll[4] = true;
				
				// Try randomly rerolling dice
				else {
					for (i = 0; i < 5; i++)
						reroll[i] = (Math.random() < 0.5);
				}
				break;

			case 2:
				// Discover if we have 1 or 2 groups of 2
				for (i = 0; i < 6; i++)
					if(repetitions[i] == 2 && i != bPos)
						twoGroups = true;

				if(twoGroups){
					
					// Find solo die
					for (i = 0; i < 6; i++) {
						if(repetitions[i] == 1){
							value = i+1;
							break;
						}
					}

					// Mark it in reroll vector
					for (i = 0; i < 5; i++)
						reroll[i] = (dice[i] == value);

				// One group of 2, tree groups of 1 - reroll three groups
				} else {
					// Mark in reroll vector
					for (i = 0; i < 5; i++)
						reroll[i] = (dice[i] != value);
				}
				break;

			case 3:
				// Guarantee to discard minimum possible value
				// Mark as reroll true the two remaining values
				for(i = 4; i >= 0; i--){
					if(dice[i] != value){
						reroll[i] = true;
						discardIndex = i;
					}
				}

				// If any of 4 & 5 of a kind has not been played yet, try it
				if(!playedPos[FOUR_OF_A_KIND] || !playedPos[FIVE_OF_A_KIND])
					break;
				else reroll[discardIndex] = false;

				// If full hand has not been played, try it
				if(!playedPos[FULL_HAND]) break;
				else reroll[discardIndex] = true;

				break;

			case 4: 
				// If 4K already played
				if(playedPos[FOUR_OF_A_KIND]){
					// Check if isolated die is at the beginning or end
					if(dice[0] == value) reroll[4] = true;
					else reroll[0] = true;
				}
				break;

			case 5:
				// If 5K and best position already played - reroll all
				if(playedPos[FIVE_OF_A_KIND] && playedPos[bPos])
					Arrays.fill(reroll, true);
				break;
			default:
				System.out.println("ERROR REROLLDICE INVALID CASE");
				break;
			}
		}

		// This reroll[] is made to sorted dice[] array, but original data is
		// unsorted. De-sort reroll[] array to match original dice data
		for (i = 0; i < 5; i++) {
			value = bkp[i];
			for (int j = 0; j < 5; j++) {
				if(dice[j] == value){
					unsortedReroll[i] = reroll[j];
				}
			}
		}



		return unsortedReroll;
	}

	private static String format(boolean[] reroll){

		int i = 0;
		char[] formatted = new char[reroll.length*2];

		for(; i < reroll.length; i++) {
			formatted[i*2] = reroll[i] ? '1' : '0';
			formatted[i*2 + 1] = ' ';
		}
		formatted[i*2 - 1] = '\0';

		return String.valueOf(formatted);
	}



	private static int checkFullHand(final int[] dice){

		int n1, n2, n1sum, n2sum;
		int special;

		/* avalio primeiro e ultimo dado*/
		n1 = dice[0];
		n2 = dice[4];
		n1sum = 0;
		n2sum = 0;
		
		//conto o numero dados com valores n1 e n2
		for(int k = 0; k < 5; k++){
			if(dice[k] == n1) n1sum++;
			if(dice[k] == n2) n2sum++;
		}
		
		//caso tenha 3 de um tipo e 2 de outro, é full hand
		special = ((n1sum == 3 && n2sum == 2) 
			  || (n1sum == 2 && n2sum == 3)) 
			  ? FULL_HAND : NOTHING;
		return special;
	}

	private static int checkStraight(final int[] dice){

		int special;
		boolean score = true;

		//verifico se eh sequencia
		for (int i = 1; i < 5; i++)
			if((dice[i-1] + 1) != (dice[i]))
				score = false;
		
		special = score ? STRAIGHT : NOTHING;

		return special;
	}

	private static int checkFourOrAKind(final int[] dice){

		int special;
		int n1, n2, n1sum, n2sum;

		/* Avalio a primeira e a penultima posicao */
		n1 = dice[0]; 
		n2 = dice[3];

		n1sum = 0;
		n2sum = 0;

		for(int k = 0; k < 5; k++){
			if(dice[k] == n1) n1sum++;
			if(dice[k] == n2) n2sum++;
		}

		// caso n1sum ou n2sum == 4, temos uma quadra de n1 ou n2
		special = (n1sum == 4 || n2sum == 4) ? FOUR_OF_A_KIND : NOTHING;
		return special;
	}

	private static int checkFiveOrAKind(final int[] dice){

		int n1sum = 0;
		int special;

		/* conta quantas vezes tenho elementos iguais*/
		for(int k = 1; k < 5; k++)
			if(dice[0] == dice[k])
				n1sum++;
		
		// n1sum == 4 => contei 4 dados iguais a dados[0]
		special = (n1sum == 4) ? FIVE_OF_A_KIND : NOTHING;
		return special;
	}

	private static int checkHand(final int[] dice, boolean[] playedPos){

		int special = NOTHING;
		
		// If position has not been played yet and hand is ordinary,
		// check next special hand
		if(playedPos[FIVE_OF_A_KIND] == false)
			special = checkFiveOrAKind(dice);
		if(playedPos[FOUR_OF_A_KIND] == false && special == NOTHING)
			special = checkFourOrAKind(dice);
		if(playedPos[STRAIGHT] == false && special == NOTHING)
			special = checkStraight(dice);
		if(playedPos[FULL_HAND] == false && special == NOTHING)
			special = checkFullHand(dice);

		return special;
	}

	private static int evaluateOrdinaryPos(final int[] dice, boolean[] playedPos){

		// Zero-initialized array
		int[] repetitions = new int[6];
		int i = 0, j = 0, counter = 0;
		int biggest, bPos = 0;
		int value;
		boolean bestFit = false;

		// Calculate repetitions
		value = dice[i];
		for (i = 0; i < 5; i++) {
			if(value == dice[i]) repetitions[value-1]++;
			else {
				value = dice[i];
				repetitions[value-1]++;
			}
		}

		// Repetitions array done, find best-fit
		while(!bestFit){

			biggest = 0;
			bPos = 0;

			/* Try to maximize gain */
			for(i = 0; i < 6; i++) {
				// Try to maximize score in case of tie
				if(repetitions[i] >= biggest){
					biggest = repetitions[i];
					bPos = i;
				}
			}

			/* Try to minimize loss*/
 			// Find first zero position to minimize score loss in
			// case all normal positions are occupied
			if(biggest == 0){
				i = 0;
				while(repetitions[i] != 0) i++;
				bPos = i;
			}

			// If position has already been played
			if(playedPos[bPos])
				repetitions[bPos] = -1; // Invalidate position
			else bestFit = true;
			
			// If all non-special positions are occupied
			counter++;
			if(counter == 6) return OCCUPIED;
		}

		return bPos;
	}

	private static int calculatePosition(boolean[] playedPos, String canonicalMsg){

		int pos;
		int[] dice = canonicalRepToInt(canonicalMsg);
		
		Arrays.sort(dice);

		pos = checkHand(dice, playedPos);

		// Non-special positions
		if(pos == NOTHING)
			pos = evaluateOrdinaryPos(dice, playedPos);
		if(pos == OCCUPIED){
			// Find minimum score special position
			for (int i = 6; i < 10; i++){
				if(!playedPos[i]){
					pos = i;
					break;
				}
			}
		}

		return pos;
	}
}
