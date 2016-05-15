package bozo;

/**
 * Essa é uma classe auxiliar que permite gerencia um conjunto de vários dados simultaneamente. Operações como rolar alguns dos dados ou exibir o resultado de todos eles, são implementadas.
 * @author Rafael Augusto Monteiro - 9293095 e Lucas Soares - 9293265
 */
public class RolaDados {
    private int ndados;
    private Dado[] dados_array;
    private Random r = new Random();
    
    /**
     * Construtor que cria e armazena vários objetos do tipo Dado. Usa para isso o construtor padrão daquela classe, ou seja, um dado de 6 lados e gerando sempre uma semente aleatória para o gerador de números aleatórios. Os dados criados podem ser referenciados por números, entre 1 e n.
     * @param n Número de dados a serem criados.
     */
    public RolaDados(int n){
        dados_array = new Dado[n];
        for(int i = 0; i < n; i++){
            long seed = r.getIntRand();
            dados_array[i] = new Dado(seed);
        }
        ndados = n;
    }
    
    /**
     * Rola todos os dados.
     * @return Retorna o valor de cada um dos dados, inclusive os que não foram rolados. Nesse caso, o valor retornado é o valor anterior que ele já possuia.
     */
    public int[] rolar(){
        int[] dadosRodados = new int[ndados];
        for(int i = 0; i < ndados; i++)
            dadosRodados[i] = dados_array[i].rolar();

        return dadosRodados;
    }
	
	/**
     * Retorna os valores dos dados
     * @return Retorna um vetor de inteiros com os valores dos dados
     */
    public int[] getDados(){
        int[] dadosRodados = new int[ndados];
        for(int i = 0; i < ndados; i++)
            dadosRodados[i] = dados_array[i].getLado();

        return dadosRodados;
    }
    
    /**
     * Rola alguns dos dados.
     * @param quais É um array de booleanos que indica quais dados devem ser rolados. Cada posição representa um dos dados. Ou seja, a posição 0 do array indica se o dado 1 deve ser rolado ou não, e assim por diante
     * @return Retorna o valor de cada um dos dados, inclusive os que não foram rolados. Nesse caso, o valor retornado é o valor anterior que ele já possuia.
     * @throws java.lang.IllegalArgumentException Caso "quais" seja maior que 6
     */
    public int[] rolar(boolean quais[]) throws java.lang.IllegalArgumentException {
        
        if(quais.length > 6)
            throw new java.lang.IllegalArgumentException();
        
        int[] dadosRodados = new int[ndados];
        for(int i = 0; i < ndados; i++)
            if(quais[i])
                dadosRodados[i] = dados_array[i].rolar();
            else dadosRodados[i] = dados_array[i].getLado();

        return dadosRodados;
    }
    
    /**
     * Rola alguns dos dados.
     * @param str É um String que possui o número dos dados a serem rolados. Por exemplo "1 4 5" indica que os dados 1 4 e cinco devem ser rolados. Os números devem ser separados por espaços. Se o valor passado no string estiver fora do intervalo válido, ele é ignorado simplesmente.
     * @return Retorna o valor de cada um dos dados, inclusive os que não foram rolados. Nesse caso, o valor retornado é o valor anterior que ele já possuia.
     * @throws IllegalArgumentException caso mais que 5 dados ou menos que 1
     */
    public int[] rolar(String str) throws java.lang.IllegalArgumentException {
        
        int i;
        int[] dadosRodados = new int[ndados];
        int[] reroll = new int[5];
        String[] resultado;
        resultado = str.split(" ");

        for(i = 0; i < resultado.length; i++) {

            reroll[i] = Integer.parseInt(resultado[i]);
            if(reroll[i] < 1 || reroll[i] > 5)
                throw new IllegalArgumentException("Out of range");
        }

        for (i = 0; i < this.ndados; i++)
            dadosRodados[i] = this.dados_array[i].getLado();

        for(i = 0; i < resultado.length; i++)
            dadosRodados[reroll[i] - 1] = this.dados_array[reroll[i] - 1].rolar();

        return dadosRodados;
    }
    
    /**
     * Usa a representação em string do dados, para mostrar o valor de todos os dados do conjunto. Exibe os dados horisontalmente, por exemplo:
     *  <pre>1          2          3          4          5
     *  +-----+    +-----+    +-----+    +-----+    +-----+    
     *  |*   *|    |     |    |*    |    |*    |    |*   *|    
     *  |  *  |    |  *  |    |     |    |  *  |    |     |    
     *  |*   *|    |     |    |    *|    |    *|    |*   *|    
     *  +-----+    +-----+    +-----+    +-----+    +-----+   </pre>
     */
    @Override
    public String toString(){

        int index;
        
        String result = "";
        
        String stLine = "1          2          3          4          5\n";
        String ndLine = "+-----+    +-----+    +-----+    +-----+    +-----+    \n";
        
        String[][] faces = 
        {
        {"|     |    ", 
           "|  *  |    ",
           "|     |    "},
        
        {"|*    |    ",
           "|     |    ",
           "|    *|    "},
        
        {"|*    |    ",
           "|  *  |    ",
           "|    *|    "},
        
        {"|*   *|    ",
           "|     |    ",
           "|*   *|    "},
        
        {"|*   *|    ",
           "|  *  |    ",
           "|*   *|    "},
        
        {"|*   *|    ",
           "|*   *|    ",
           "|*   *|    "}
        };

        result += stLine;
        result += ndLine;

        for (int line = 0; line < 3; line++) {
            for (int column = 0; column < 5; column++) {
                
                index = this.dados_array[column].getLado()-1;
                result += faces[index][line];
            }
            result += "\n";
        }

        result += ndLine;
        return result;
    }
}