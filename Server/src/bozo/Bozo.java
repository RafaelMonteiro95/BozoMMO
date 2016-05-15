package bozo;

import javax.swing.SwingUtilities;

/**
 * Essa é a classe inicial do programa Bozó.
 * @author Rafael Augusto Monteiro - 9293095 e Lucas Soares - 9293265
 */
public class Bozo {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception Exception
     */
    public static void main(String[] args) throws Exception {
        
        BozoGUI u = new BozoGUI();
        
        SwingUtilities.invokeLater(() -> u.mostraJanela());
    }
}