class UndirectedGraphFactory {
    static UndirectedGraph fromEdges(int V, int[][] edges) {
        UndirectedGraphBuilder builder = new UndirectedGraphBuilder(V);
        for (int[] edge : edges) {
            builder.addEdge(edge[0], edge[1]);
        }
        return builder.build();
    }
}
