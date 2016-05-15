package bozo;

import java.util.Calendar;

/**
 * gerador simples de números aleatórios.
 * @author Rafael Augusto Monteiro - 9293095
 */
public class Random {
    
    private long p = 2147483648l;
    private long m = 843314861;
    private long a = 453816693;
    
    private long xi = 1023;
    
    /**
     * Cria objeto com um número qualquer de lados
     * @param k - número de lados do dado
     */
    public Random(long k){
        xi = k;
    }
    
    /**
     * Construtor que usa uma semente aleatória, adquerida usando o método Calendar.getTimeInMillis().
     */
    public Random(){
        xi = Calendar.getInstance().getTimeInMillis() % p;
    }
    
    /**
     * Retorna um número aleatório no intervalo (0,1[
     * @return o número gerado.
     */
    public double getRand(){
        xi = (a + m * xi) % p;
        double d = xi;
        return d / p; 
    }

    /**
     * Retorna um valor inteiro no intervalo (0,max[
     * @param max O valor limite para a geração do número inteiro
     * @return o número gerado.
     */
    public int getIntRand(int max){
        double d = getRand() * max;
        return (int) d % max;
    }
    
    /**
     * Retorna um valor inteiro no intervalo ]min,max[
     * @param min O valor minimo para a geração do número inteiro
     * @param max O valor maximo para a geração do número inteiro
     * @return o número gerado.
     * @throws IllegalArgumentException Caso min maior que max
     */
    public int getIntRand(int min, int max) throws IllegalArgumentException {
        if(max <= min){
            throw new IllegalArgumentException("Invalid parameters");
        }
        return getIntRand(max-min) + min;
    }
    
    /**
     * Retorna um valor inteiro qualquer
     * @return o número gerado.
     */
    public int getIntRand(){
        return getIntRand(Integer.MAX_VALUE);
    }
    
    /**
     * Permite alterar a semente de geração de números aleatórios. Supostamente deve ser chamada antes de iniciar a geração, mas se for chamado a qualquer instante, reseta o valor da semante
     * @param semente  o valor da nova semente de geração
     */
    public void setSemente(int semente){
        xi = semente;
    }
    
    @Override
    public String toString(){
        return Integer.toString((int) xi);
    }
}
