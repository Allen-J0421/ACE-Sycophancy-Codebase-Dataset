package matrixchain;

public final class MatrixChainCli {

    public static void main(String[] args) {
        if (args.length == 0) {
            MatrixDimensions[] example = {
                new MatrixDimensions(2, 1),
                new MatrixDimensions(1, 3),
                new MatrixDimensions(3, 4)
            };
            System.out.println(MatrixChainSolver.solve(example));
            return;
        }
        try {
            MatrixChainArguments arguments = MatrixChainArguments.parse(args);
            System.out.println(MatrixChainSolver.solve(arguments.matrices()));
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Usage: java matrixchain.MatrixChainCli <dim1> <dim2> ... <dimN>");
            System.exit(1);
        }
    }
}
