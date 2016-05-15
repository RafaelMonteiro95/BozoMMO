/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bozo;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BozoGUI{
    JFrame janela;
    JPanel princ;
    JPanel esquerdo;
    JPanel esquerdo_;
    JPanel direito;
    JPanel direito_;
    JButton rolar;
    JLabel placar;
    JLabel teste;
        
    RolaDados rd;
    Placar p;

    int pos; //posicao que adiciono no placar
    int nRolls; //numero de vezes que realizei as rolagens
    int rounds; //rodadas do jogo
    int[] rolledDice; //valor dos dados rolados
    String dice[]; //array de strings formatadas de dados
    String unformatted; //string nao formatada de dados
    boolean error; //flag de erro
    
    Integer plac = 0;
    
    public BozoGUI(){
        rd = new RolaDados(5);
        p = new Placar();
        rolledDice = new int[5];
        dice = new String[5];
    }
    
        /**
     * Altera o look and feel para o padrao do SO
     * e é isso aí
     */
    private void setLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(janela);
    }
    
    /**
     * Prepara o JFrame
     * Aqui preciso instanciar um objeto JFrame
     * Pode ser substituido pelo construtor caso eu herde a classe JFrame
     */
    private void preparaJanela(){
        janela = new JFrame("Bozó");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setMinimumSize(new Dimension(200,100));
    }
    
    private void preparaPainelPrincipal(){
        princ = new JPanel();        
        princ.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        janela.add(princ);
    }
    
    private void preparaPainelEsquerdo(){
        esquerdo_ = new JPanel();
        esquerdo = new JPanel();
        
        esquerdo_.setLayout(new BoxLayout(esquerdo_, BoxLayout.Y_AXIS));//top level
        esquerdo.setLayout(new FlowLayout(FlowLayout.CENTER));//ta contido no acima
        
        esquerdo_.add(esquerdo);
        
        princ.add(esquerdo_);
    }
    
    private void preparaPainelDireito(){
        direito_ = new JPanel();
        direito = new JPanel();
        
        direito_.setLayout(new BoxLayout(direito_, BoxLayout.Y_AXIS));//top level
        direito.setLayout(new FlowLayout(FlowLayout.CENTER));//ta contido no acima
        
        direito_.add(direito);
        
        princ.add(direito);
        princ.setSize(400, 400);
    }
    
    private void preparaPlacar(){
        placar = new JLabel("              ");
        esquerdo.add(placar);
    }
    
    private void preparaBotaoRolar(){
        rolar = new JButton("Rolar dados");
        esquerdo_.add(rolar);
        rolar.addActionListener((ActionEvent e) -> {
            
            rolledDice = rd.rolar();
            placar.setText(rolledDice[0] + " " +
                           rolledDice[1] + " " +
                           rolledDice[2] + " " +  
                           rolledDice[3] + " " +
                           rolledDice[4]);
            nRolls++;
            if(nRolls >= 3){
                rolar.setEnabled(false);
            }
        });
    }

    
    public void mostraJanela(){
        
        preparaJanela();
        preparaPainelPrincipal();
        preparaPainelEsquerdo();
        preparaPainelDireito();
        preparaPlacar();
        preparaBotaoRolar();
        setLookAndFeel();
        janela.pack();
        janela.setVisible(true);
    }
    
    public void gameLoop() throws Exception{
           
        for(int i = 0 ; i < 10; i++){
            System.out.println(i+1 + "a Rodada *********");
            //inciando rodada
            System.out.println("Pressione ENTER para rolar os dados");
            EntradaTeclado.readString();
            //imprimindo primeiros dados rolados
            rolledDice = rd.rolar();
            System.out.println(rd);
            
            //setando numero de vezes que rolei o dado para 0
            nRolls = 0;
            do{
                //lendo dados a serem rolados
                System.out.println("Escolha os dados que quiser mudar, ou 0 para nao mudar nada");              
                unformatted = EntradaTeclado.readString();
                
                //splito os dados para dice
                for (int k = 0; k < unformatted.length(); k++)
                    dice = unformatted.split(" ");
                
                //caso dice = 0 paro de rolar os dados
                if(Integer.parseInt(dice[0]) == 0) break;

                //rolo os dados
                try {
                    rolledDice = rd.rolar(unformatted);
                } catch (Exception e){
                    continue;
                }
                //incremento numero de rolagens e imprimo os dados
                nRolls++;
                System.out.println(rd);
                
            } while(nRolls < 2);
            
            //imprimo o placar no final da rodada
            System.out.println("Placar: \n" + p);
            
            //escolhendo a posicao de insercao no placar
            do{
                System.out.println("Escolha a posicao do placar");
                pos = EntradaTeclado.readUInt();
                //soh insiro caso nao haja erro
                try {
                    p.add(pos, rolledDice);
                    error = false;
                
                } catch (Exception e){
                    error = true;
                }
            } while(error);
            
            //imprimo placar
            System.out.println(p);
        }
        
        System.out.println("Placar final: " + p.getScore());
    }
    
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception Exception
     */
    public static void main(String[] args) throws Exception {
        
        BozoGUI u = new BozoGUI();
        
        SwingUtilities.invokeLater(() -> u.mostraJanela());
    }
}
