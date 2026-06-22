public final class MST {
    private MST() {
    }

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.fromAdjacencyMatrix(new int[][] {
            {0, 2, 0, 6, 0},
            {2, 0, 3, 8, 5},
            {0, 3, 0, 0, 7},
            {6, 8, 0, 0, 9},
            {0, 5, 7, 9, 0}
        });

        MstResult result = PrimsMinimumSpanningTree.compute(graph);
        System.out.println(MstFormatter.format(result));
    }
}
