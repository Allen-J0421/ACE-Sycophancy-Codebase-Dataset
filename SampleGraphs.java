/**
 * Provides ready-made sample graphs for demonstrations and tests.
 */
public final class SampleGraphs {

    private SampleGraphs() {
    }

    /**
     * The five-vertex weighted graph used by the canonical Prim's example.
     *
     * <pre>
     *         2     3
     *     (0)---(1)---(2)
     *      | \   | \
     *     6|  8\ |5 \7
     *      |    \|   \
     *     (3)----+---(4)
     *          9
     * </pre>
     */
    public static WeightedGraph classicExample() {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 },
        };
        return WeightedGraph.fromAdjacencyMatrix(matrix);
    }
}
