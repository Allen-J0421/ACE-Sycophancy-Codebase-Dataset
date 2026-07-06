interface GraphFactory {
    GraphBuilder newBuilder(int V);

    default Graph fromEdges(int V, int[][] edges) {
        GraphBuilder builder = newBuilder(V);
        for (int[] edge : edges) {
            builder.addEdge(edge[0], edge[1]);
        }
        return builder.build();
    }
}
