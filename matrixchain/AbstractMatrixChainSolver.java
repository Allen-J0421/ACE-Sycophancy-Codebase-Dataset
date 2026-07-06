package matrixchain;

public abstract class AbstractMatrixChainSolver implements MatrixChainSolver {

    protected int[] dims;
    protected int n;
    protected SplitTable split;

    @Override
    public final Result solve(int[] dims) {
        this.dims = dims;
        this.n = dims.length;
        this.split = new SplitTable(n);
        int minCost = computeMinCost();
        return new Result(minCost, split);
    }

    protected abstract int computeMinCost();

    protected int multiplicationCost(int i, int k, int j) {
        return dims[i] * dims[k] * dims[j];
    }
}
