package bozo;

import java.util.Arrays;

/**
 * Esta classe representa o placar de um jogo de Bozó. Permite que combinações de dados sejam alocadas às posições e mantém o escore de um jogador.
 * @author Rafael Augusto Monteiro - 9293095 e Lucas Soares - 9293265
 */
public class Placar {
    private int scores[];
    private boolean rolls[];
    
    /**
     * Construtor padrao
     */
    public Placar(){
        scores = new int[10];
        rolls = new boolean[5];

        for (int i = 0; i < 10; i++)
            scores[i] = -1;
    }
    
    /**
     * Adiciona uma sequencia de dados em uma determinada posição do placar.
     * @param posicao Posição a ser preenchida. As posições 1 a 6 correspondem às quantidas de uns até seis, ou seja, as laterais do placar. As outas posições são: 7 - full hand; 8 - sequencia; 9 - quadra; e 10 - quina
     * @param dados um array de inteiros, de tamanho 5. Cada posição corresponde a um valor de um dado. Supões-se que cada dado pode ter valor entre 1 e 6.
     * @throws IllegalArgumentException Se a posição estiver ocupada ou se for passado um valor de posição inválido, essa exceção é lançada. Não é feita nenhuma verificação quanto ao tamanho do array nem quanto ao seu conteúdo.
     */
    public void add(int posicao, int[] dados) throws java.lang.IllegalArgumentException{
        
        int n1, n2, n1sum, n2sum;
        boolean score = false;

        // Position already occupied
        if(scores[posicao-1] != -1) throw new IllegalArgumentException();

        // Ordeno os valores dos dados para facilitar minha vida
        Arrays.sort(dados);

        switch(posicao){
            
            // Full hand
            case 7:
                /* avalio primeiro e ultimo dado*/
                n1 = dados[0];
                n2 = dados[4];
                n1sum = 0;
                n2sum = 0;
                
                //conto o numero dados com valores n1 e n2
                for(int k = 0; k < 5; k++){
                    if(dados[k] == n1) n1sum++;
                    if(dados[k] == n2) n2sum++;
                }
                
                //caso tenha 3 de um tipo e 2 de outro, é full hand
                scores[6] = ((n1sum == 3 && n2sum == 2) 
                            || (n1sum == 2 && n2sum == 3)) ? 15 : 0;
                    
                break;
            // Sequencia
            case 8:
                score = true;
                //verifico se eh sequencia
                for (int i = 1; i < 5; i++){
                    if((dados[i-1] + 1) != (dados[i])) score = false;
                }
                
                //caso chegue ateh aqui, eh sequencia.
                scores[7] = score ? 20 : 0;
                break;

            // Quadra
            case 9:
                
                /* Avalio a primeira e a penultima posicao */
                n1 = dados[0]; 
                n2 = dados[3];

                n1sum = 0;
                n2sum = 0;

                for(int k = 0; k < 5; k++){
                    if(dados[k] == n1) n1sum++;
                    if(dados[k] == n2) n2sum++;
                }
                // caso n1sum ou n2sum == 4, temos uma quadra de n1 ou n2
                scores[8] = (n1sum == 4 || n2sum == 4) ? 30 : 0;
                break;

            // Quina        
            case 10:
                /* conta quantas vezes tenho elementos iguais*/
                n1sum = 0;
                for(int k = 1; k < 5; k++){
                    if(dados[0] == dados[k]) n1sum++;
                }
                // n1sum == 4 => contei 4 dados iguais a dados[0]
                scores[9] = (n1sum == 4) ? 40 : 0;
                break;

            // 1 2 3 - 4 5 6
            default:
                n1sum = 0;
                //conto o numero de dados com valor "posicao"
                for (int i = 0; i < 5; i++)
                    if(dados[i] == posicao) 
                        n1sum += posicao; 

                scores[posicao-1] = n1sum;
                break;
        }
    }
    
    /**
     * Computa a soma dos valores obtidos, considerando apenas as posições que já estão ocupadas.
     * @return valor do score
     */
    public int getScore(){
        int i, sum = 0;
        for(i = 0; i < 10; i++){
            sum += (scores[i] >= 0) ? scores[i]: 0;
        }
        return sum;
    }
     /**
      * A representação na forma de string, mostra o placar completo, indicando quais são as posições livres (com seus respectivos números) e o valor obtido nas posições já ocupadas. Por exemplo:
      * <pre> (1)    |   (7)    |   (4) 
 --------------------------
 (2)    |   20     |   (5) 
 --------------------------
 (3)    |   30     |   (6) 
--------------------------
        |   (10)   |
        +----------+ 
</pre>
* mostra as posições 8 (sequencia) e 9 (quadra) ocupadas.
      */
    @Override
    public String toString(){
        int i;
        String[] temp = new String[8];
        String result = "";
        temp[0] = "";
        temp[1] = " --------------------------\n";
        temp[2] = "";
        temp[3] = " --------------------------\n";
        temp[4] = "";
        temp[5] = " --------------------------\n";
        temp[6] = "";
        temp[7] = "       +--------+\n";
        
        //preparando primeira linha com valores
        temp[0] = this.scores[0] < 0 ? "(1)    |": "  " + this.scores[0] + "    |";
        temp[0] += this.scores[6] < 0 ? "   (7)\t|": "   " + this.scores[6] + "\t|";
        temp[0] += this.scores[3] < 0 ? "   (4)\n"     : "   " + this.scores[3] + "\n";
        //preparando segunda linha com valores
        temp[2] = this.scores[1] < 0 ? "(2)    |": "  " + this.scores[1] + "    |";
        temp[2] += this.scores[7] < 0 ? "   (8)\t|": "   " + this.scores[7] + "\t|";
        temp[2] += this.scores[4] < 0 ? "   (5)\n"     : "   " + this.scores[4] + "\n";
        //preparando terceira linha com valores
        temp[4] = this.scores[2] < 0 ? "(3)    |": "  " + this.scores[2] + "    |";
        temp[4] += this.scores[8] < 0 ? "   (9)\t|": "   " + this.scores[8] + "\t|";
        temp[4] += this.scores[5] < 0 ? "   (6)\n"     : "   " + this.scores[5] + "\n";
        //preparando quarta linha com valores
        temp[6] = this.scores[9] < 0 ? "       |  (10)\t|\n":
                "       |  " + this.scores[9] + "\t|\n";
        for (i = 0; i < 8; i++){
            result += temp[i];
        }
        return result;
    }
}
