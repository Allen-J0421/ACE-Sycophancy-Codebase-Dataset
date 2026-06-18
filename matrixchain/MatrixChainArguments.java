package matrixchain;

final class MatrixChainArguments {
    final MatrixDimensions[] matrices;

    private MatrixChainArguments(MatrixDimensions[] matrices) {
        this.matrices = matrices;
    }

    static MatrixChainArguments parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "At least 2 dimension values are required to form one matrix, got: " + args.length);
        }
        int[] dims = parseDims(args);
        MatrixDimensions[] matrices = new MatrixDimensions[dims.length - 1];
        for (int i = 0; i < matrices.length; i++) {
            matrices[i] = new MatrixDimensions(dims[i], dims[i + 1]);
        }
        return new MatrixChainArguments(matrices);
    }

    private static int[] parseDims(String[] args) {
        int[] dims = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                dims[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid dimension value: '" + args[i] + "'");
            }
            if (dims[i] <= 0) {
                throw new IllegalArgumentException("Dimension values must be positive, got: " + dims[i]);
            }
        }
        return dims;
    }
}
