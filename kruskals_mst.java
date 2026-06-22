final class KruskalMST {
    private static final MinimumSpanningTreeCalculator CALCULATOR =
        new KruskalMinimumSpanningTreeCalculator();

    private KruskalMST() {
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        Graph graph = Graph.fromRawEdges(vertexCount, rawEdges);
        return CALCULATOR.compute(graph).totalWeight();
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };

        System.out.println(kruskalsMST(4, edges));
    }
}
