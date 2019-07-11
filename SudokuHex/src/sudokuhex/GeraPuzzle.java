
package sudokuhex;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * http://www.sudokuoftheday.com/techniques/
 */
public class GeraPuzzle {

    private int[][] iaArray;
    private static final int iN = 16; //Hexadecimal Puzzle
    private String sTipoPuzzle = "";

    public GeraPuzzle(String sTipoPuzzle) {
        this.sTipoPuzzle = sTipoPuzzle;
    }

    public int[][] getIaArray() {
        return iaArray;
    }

    public void setIaArray(int[][] iaArray) {
        this.iaArray = iaArray;
    }

    public String getsTipoPuzzle() {
        return sTipoPuzzle;
    }

    public void setsTipoPuzzle(String sTipoPuzzle) {
        this.sTipoPuzzle = sTipoPuzzle;
    }


    public void criaHexadoku() {
        iaArray = new int[iN][iN];
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                iaArray[i][j] = -1;
            }
        }

        // Forçar aleatoriedade ao array
        criaAleatoriedade(iaArray);

        resolvePuzzle(iaArray);
    }

    public boolean resolvePuzzle(int[][] iaArray) {

        int iLinha = -1, iCol = -1;

        // Puzzle resolvido
        if (!faltaPreencher(iaArray)) {
            return true;
        }

        // Verificar linha e coluna por preencher
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                if (iaArray[i][j] == -1) {
                    iLinha = i;
                    iCol = j;
                }
            }
        }
        // Avaliar valores possiveis para célula
        for (int iValor = 0; iValor < iN; iValor++) {
            if (valorValido(iaArray, iLinha, iCol, iValor)) {
                //Atribuir valor a célula
                iaArray[iLinha][iCol] = iValor;
                //Verificar se valor introduzido permite resolução de puzzle
                if (resolvePuzzle(iaArray)) {
                    return true;
                } // Backtracking, o valor introduzido não permitiu a resolução do puzzle
                else {
                    iaArray[iLinha][iCol] = -1;
                }
            }
        }
        return false;

    }

    // Tecnica de resolução fácil
    public boolean singleCandidate(int[][] iaArray) {

        int iAux = -1;

        // Puzzle resolvido
        if (!faltaPreencher(iaArray)) {
            return true;
        }

        // Verificar linha e coluna por preencher
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                if (iaArray[i][j] == -1) {
                    iAux = numPoss(iaArray, i, j);
                    if (iAux != -1) {
                        iaArray[i][j] = iAux;
                        if (singleCandidate(iaArray)) {
                            return true;
                        } // Tentar outra técnica
                        else {
                            return false;
                        }
                    }

                }
            }
        }

        return false;

    }

    public int numPoss(int[][] iaArray, int iLinha, int iCol) {
        int valorPoss = -1;
        boolean bFlag = true;
        for (int iValor = 0; iValor < iN; iValor++) {
            if (valorValido(iaArray, iLinha, iCol, iValor)) {
                if (!bFlag) {
                    return -1;
                }
                if (bFlag) {
                    valorPoss = iValor;
                    bFlag = false;
                }
            }
        }
        return valorPoss;
    }

//-----------------------------------------------------------------------------------
    // Tecnica de resolução média
    public boolean candidateLines(int[][] iaArray) {

        ArrayList<Integer> alAux = null;
        int iAux = -1;

        // Puzzle resolvido
        if (!faltaPreencher(iaArray)) {
            return true;
        }

        // Verificar linha e coluna por preencher
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                if (iaArray[i][j] == -1) {
                    alAux = arrayNumPoss(iaArray, i, j);
                    //Avaliar linha, com nova informação
                    for (int k = 0; k < iN; k++) {
                        if (iaArray[i][k] == -1) {
                            iAux = numPoss2(iaArray, i, k, alAux);
                            if (iAux != -1) {
                                iaArray[i][k] = iAux;
                                if (candidateLines(iaArray)) {
                                    return true;
                                } // As técnicas usadas não foram suficientes para resolver
                                else {
                                    return false;
                                }
                            }
                        }
                    }

                }
            }
        }

        return false;

    }

    public ArrayList<Integer> arrayNumPoss(int[][] iaArray, int iLinha, int iCol) {
        ArrayList al = new ArrayList<>();
        for (int iValor = 0; iValor < iN; iValor++) {
            if (valorValido(iaArray, iLinha, iCol, iValor)) {
                al.add(iValor);
            }
        }
        return al;
    }

    public int numPoss2(int[][] iaArray, int iLinha, int iCol, ArrayList<Integer> al) {
        int valorPoss = -1;
        boolean bFlag = true;
        for (int iValor = 0; iValor < iN; iValor++) {
            if (!al.contains(iValor)) {
                if (valorValido(iaArray, iLinha, iCol, iValor)) {
                    if (!bFlag) {
                        return -1;
                    }
                    if (bFlag) {
                        valorPoss = iValor;
                        bFlag = false;
                    }
                }
            }

        }
        return valorPoss;
    }

    public int totCellVazias(int[][] iaArray) {
        int tot = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (iaArray[i][j] == -1) {
                    tot++;
                }
            }
        }
        return tot;
    }

//-----------------------------------------------------------------------------------
    //Avalia se numero pode ser inserido
    public boolean valorValido(int[][] iaArray, int iLinha, int iColuna, int iNumb) {

        int i;
        //Avalia linhas e colunas
        for (i = 0; i < iN; i++) {
            if (iaArray[iLinha][i] == iNumb) {
                return false;
            }
            if (iaArray[i][iColuna] == iNumb) {
                return false;
            }
        }

        //Associar iLinha e iColuna a seção
        int iLinhaSeccao = 4 * (iLinha / 4);
        int iColunaSeccao = 4 * (iColuna / 4);

        //Avalia secçao 4x4 
        for (i = iLinhaSeccao; i < iLinhaSeccao + 4; i++) {
            for (int j = iColunaSeccao; j < iColunaSeccao + 4; j++) {
                if (iaArray[i][j] == iNumb) {
                    return false;
                }
            }
        }

        return true;
    }

    // Perceber se grelha já foi completada
    public boolean faltaPreencher(int[][] iaArray) {

        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                if (iaArray[i][j] == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    // Preenche a ultima linha aleatoriamente
    public void criaAleatoriedade(int[][] iaArray) {

        // Array de valores possiveis
        ArrayList<Integer> alNumbPoss = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            alNumbPoss.add(i);
        }
        
        int iValor;
        Random rand = new Random();
        // Preencher aleatoriamente as diagonais
        for (int i = 0; i < 4; i++) {
            preencheMatriz(i*4,i*4);
        }

    }
    
    public void preencheMatriz(int iLin, int iCol){
        
        ArrayList<Integer> alNumbPoss = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            alNumbPoss.add(i);
        }
                    
        int iValor;
        Random rand = new Random();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                iValor = rand.nextInt(alNumbPoss.size());
                iaArray[iLin+i][iCol+j] = alNumbPoss.get(iValor);
                alNumbPoss.remove(iValor);
            }
        }
    }
            

    // Avalia a dificuldade do puzzle
    public int[][] puzzleGeradoDificuldade() {
        boolean bFlag;
        int[][] iaNewPuzzle = null, iaNewPuzzleRemovido = null;

        while (true) {

            iaNewPuzzle = copiaArray(iaArray);
            retiraNum(iaNewPuzzle);
            iaNewPuzzleRemovido = copiaArray(iaNewPuzzle);
            bFlag = singleCandidate(iaNewPuzzle);
            if (bFlag) {
                if (sTipoPuzzle.equals("Fácil")) {
                    return iaNewPuzzleRemovido;
                }
            } else {
                if (sTipoPuzzle.equals("Médio") || sTipoPuzzle.equals("Difícil"))
                    return iaNewPuzzleRemovido;
                //bFlag = candidateLines(iaNewPuzzle);
                /*if (bFlag) {
                    if (sTipoPuzzle.equals("Médio")) {
                        return iaNewPuzzleRemovido;
                    }
                } else {
                        if (sTipoPuzzle.equals("Difícil")) {
                            return iaNewPuzzleRemovido;
                        }
                    }
                }*/

            }
        }

    }

    // Retira aleatoriamente números do puzzle
    public void retiraNum(int[][] iaArray) {
        int iNumb = 6;
        if (sTipoPuzzle.equals("Difícil")) {
            iNumb = 4;
        }
        if (sTipoPuzzle.equals("Médio")) {
            iNumb = 5;
        }

        Random rand = new Random();
        int aux, tot1 = 0, tot0 = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                aux = rand.nextInt(9);
                if (aux >= iNumb ) {
                    iaArray[i][j] = -1;
                }

            }
        }
    }

    public static void printArray(int[][] iaArray) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(iaArray[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    public int[][] copiaArray(int[][] iaArray) {
        int[][] iaNewArray = new int[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                iaNewArray[i][j] = iaArray[i][j];
            }
        }
        return iaNewArray;
    }

}
