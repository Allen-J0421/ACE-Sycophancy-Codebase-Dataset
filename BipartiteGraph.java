final class BipartiteGraph {

    private BipartiteGraph() {
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        return BipartiteAnalyzer.isBipartite(UndirectedGraphBuilder.fromEdgeList(vertexCount, edges));
    }

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }
}
