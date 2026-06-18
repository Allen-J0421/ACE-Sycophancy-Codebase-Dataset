package matrixchain;

final class MatrixChainArguments {
    private final MatrixDimensions[] matrices;

    private MatrixChainArguments(MatrixDimensions[] matrices) {
        this.matrices = matrices;
    }

    MatrixDimensions[] matrices() {
        return matrices.clone();
    }

    static MatrixChainArguments parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "At least 2 dimension values are required to form one matrix, got: " + args.length);
        }
        MatrixDimensions[] matrices = new MatrixDimensions[args.length - 1];
        int prev = parsePositiveDim(args[0]);
        for (int i = 1; i < args.length; i++) {
            int curr = parsePositiveDim(args[i]);
            matrices[i - 1] = new MatrixDimensions(prev, curr);
            prev = curr;
        }
        return new MatrixChainArguments(matrices);
    }

    private static int parsePositiveDim(String s) {
        int dim;
        try {
            dim = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid dimension value: '" + s + "'");
        }
        if (dim <= 0) throw new IllegalArgumentException("Dimension values must be positive, got: " + dim);
        return dim;
    }
}
