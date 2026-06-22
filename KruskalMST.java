final class KruskalMST {
    private KruskalMST() {
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        return compute(vertexCount, rawEdges).totalWeight();
    }

    static MinimumSpanningTree compute(int vertexCount, int[][] rawEdges) {
        return compute(Graphs.fromRawEdges(vertexCount, rawEdges));
    }

    static MinimumSpanningTree compute(Graph graph) {
        return KruskalMinimumSpanningTreeFinder.find(graph);
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };

        System.out.println(kruskalsMST(4, edges));
    }
}
