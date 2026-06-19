package matrixchain;

/**
 * Command-line entry point. Solves the chain given as integer arguments, or a
 * small built-in demo chain when no arguments are supplied.
 *
 * <pre>
 *   java matrixchain.MatrixChainCli 2 1 3 4
 * </pre>
 */
public final class MatrixChainCli {

    private static final int[] DEMO_CHAIN = { 2, 1, 3, 4 };

    public static void main(String[] args) {
        int[] dimensions = args.length > 0 ? parse(args) : DEMO_CHAIN;

        MatrixDimensions chain = MatrixDimensions.of(dimensions);
        MatrixChainResult result = new MatrixChainSolver().solve(chain);

        System.out.println("Dimensions:    " + chain);
        System.out.println("Optimal order: " + result.parenthesization());
        System.out.println("Minimum cost:  " + result.minimumCost());
    }

    private static int[] parse(String[] args) {
        int[] values = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                values[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("not an integer: '" + args[i] + "'", e);
            }
        }
        return values;
    }

    private MatrixChainCli() {
    }
}
