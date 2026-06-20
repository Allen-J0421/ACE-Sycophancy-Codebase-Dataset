final class TestGraphs {
    private static final int INF = FloydWarshall.NO_PATH;
    private static final int[][] ACYCLIC_THREE_NODE_GRAPH = {
        {0, 2, INF},
        {INF, 0, 3},
        {INF, INF, 0}
    };
    private static final int[][] NEGATIVE_CYCLE_GRAPH = {
        {0, 1, INF},
        {INF, 0, -2},
        {-2, INF, 0}
    };
    private static final int[][] OVERFLOW_GRAPH = {
        {0, Integer.MAX_VALUE - 1, INF},
        {INF, 0, 2},
        {INF, INF, 0}
    };
    private static final int[][] UNREACHABLE_TWO_NODE_DISTANCES = {
        {0, INF},
        {INF, 0}
    };
    private static final int[][] INVALID_RECTANGULAR_MATRIX = {
        {0},
        {1, 0}
    };
    private static final int[][] MATRIX_WITH_NULL_ROW = {
        {0},
        null
    };
    private static final int[][] NON_SQUARE_ROW_MATRIX = {
        {0, 1}
    };

    private TestGraphs() {
    }

    static int[][] acyclicThreeNodeGraph() {
        return MatrixUtils.copyRows(ACYCLIC_THREE_NODE_GRAPH);
    }

    static int[][] negativeCycleGraph() {
        return MatrixUtils.copyRows(NEGATIVE_CYCLE_GRAPH);
    }

    static int[][] overflowGraph() {
        return MatrixUtils.copyRows(OVERFLOW_GRAPH);
    }

    static int[][] unreachableTwoNodeDistances() {
        return MatrixUtils.copyRows(UNREACHABLE_TWO_NODE_DISTANCES);
    }

    static int[][] invalidRectangularMatrix() {
        return MatrixUtils.copyRows(INVALID_RECTANGULAR_MATRIX);
    }

    static int[][] matrixWithNullRow() {
        return MatrixUtils.copyRows(MATRIX_WITH_NULL_ROW);
    }

    static int[][] nonSquareRowMatrix() {
        return MatrixUtils.copyRows(NON_SQUARE_ROW_MATRIX);
    }
}
