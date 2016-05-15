package bozo; 

/**
 *Simula um dado de número de lados variados. Ao criar o objeto é possível estabelecer o número de lados. A rolagem do dado é feita por meio de um gerador de números aleatórios (Random).
 * @author Rafael Augusto Monteiro - 9293095 e Lucas Soares - 9293265
 */
public class Dado {
    
    private int faces;
    private int lastFace;
    private Random r;
    
    /**
     * Cria um dado com 6 lados (um cubo)
     */
    public Dado(){
        r = new Random();
        faces = 6;
    }
    
    /**
     * Cria um dado com 6 lados (um cubo)
     * @param seed semente para as operacoes aleatorias do dado
     */
    public Dado(long seed){
        r = new Random();
        r.setSemente((int) seed);
        faces = 6;
    }
    
    /**
     * Cria objeto com um número qualquer de lados
     * @param n número de lados do dado
     */
    public Dado(int n){
        r = new Random();
        faces = n;
    }
    
    /**
     * Recupera o último número selecionado.
     * @return o número do último lado selecionado.
     */
    public int getLado(){
        return lastFace;
    }

    /**
     * Simula a rolagem do dado por meio de um gerador aleatório. O número selecionado pode posteriormente ser recuperado com a chamada a getLado()
     * @return o número que foi sorteado
     */
    public int rolar(){
        lastFace = r.getIntRand(1,faces+1);
        return lastFace;
    }
    
    /**
     * Transforma representação do dado em String. É mostrada uma representação do dado que está para cima. Note que só funciona corretamente para dados de 6 lados. Exemplo:
     *   <pre>  +-----+    
     *  |*   *|    
     *  |  *  |    
     *  |*   *|    
     *  +-----+
        * </pre>
     * @return Um dado representado como string.
     */
    @Override
    public String toString(){
        if(faces == 6){
            switch(lastFace){
                case 1:
                    return  "+-----+    \n" +
                            "|     |    \n" +
                            "|  *  |    \n" +
                            "|     |    \n" +
                            "+-----+  ";
                case 2:
                    return  "+-----+    \n" +
                            "|   * |    \n" +
                            "|     |    \n" +
                            "| *   |    \n" +
                            "+-----+  ";
                case 3:
                    return  "+-----+    \n" +
                            "|   * |    \n" +
                            "|  *  |    \n" +
                            "| *   |    \n" +
                            "+-----+  ";
                case 4:
                    return  "+-----+    \n" +
                            "|*   *|    \n" +
                            "|     |    \n" +
                            "|*   *|    \n" +
                            "+-----+  ";
                case 5:
                    return  "+-----+    \n" +
                            "|*   *|    \n" +
                            "|  *  |    \n" +
                            "|*   *|    \n" +
                            "+-----+  ";
                case 6:
                    return  "+-----+    \n" +
                            "|*   *|    \n" +
                            "|*   *|    \n" +
                            "|*   *|    \n" +
                            "+-----+  ";
                default:
                    return "null";
            }
        } else {
            return Integer.toString(lastFace);
        }
    }
}
