package matrixchain;

import java.util.List;

public abstract class AbstractMatrixChainSolver implements MatrixChainSolver {

    protected int[] dims;
    protected int n;
    protected SplitTable split;

    @Override
    public final Result solve(List<Matrix> matrices) {
        this.dims = toDimsArray(matrices);
        this.n = dims.length;
        this.split = new SplitTable(n);
        int minCost = computeMinCost();
        return new Result(minCost, split);
    }

    private static int[] toDimsArray(List<Matrix> matrices) {
        for (int i = 0; i < matrices.size() - 1; i++) {
            if (matrices.get(i).cols() != matrices.get(i + 1).rows()) {
                throw new IllegalArgumentException(
                    "Incompatible matrices at positions " + i + " and " + (i + 1));
            }
        }
        int[] dims = new int[matrices.size() + 1];
        dims[0] = matrices.get(0).rows();
        for (int i = 0; i < matrices.size(); i++) {
            dims[i + 1] = matrices.get(i).cols();
        }
        return dims;
    }

    protected abstract int computeMinCost();

    protected int multiplicationCost(int i, int k, int j) {
        return dims[i] * dims[k] * dims[j];
    }
}
