package matrixchain;

public class SplitTable {

    private final int[][] table;

    public SplitTable(int n) {
        table = new int[n][n];
    }

    public SplitTable set(int i, int j, int k) {
        table[i][j] = k;
        return this;
    }

    public int get(int i, int j) {
        return table[i][j];
    }

    public String optimalOrder(int i, int j) {
        if (i + 1 == j) return "M" + j;
        int k = table[i][j];
        return "(" + optimalOrder(i, k) + " x " + optimalOrder(k, j) + ")";
    }
}
