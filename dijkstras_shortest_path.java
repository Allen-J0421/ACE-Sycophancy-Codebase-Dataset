class Dijkstra {
    public static void main(String[] args) {
        int src = 0;

        Graph graph = new Graph(5)
                .addEdge(0, 1, 4)
                .addEdge(0, 2, 8)
                .addEdge(1, 4, 6)
                .addEdge(1, 2, 3)
                .addEdge(2, 3, 2)
                .addEdge(3, 4, 10);

        PathfindingResult result = Pathfinder.dijkstra(graph, src);
        for (int v = 0; v < graph.size(); v++)
            System.out.print(result.distanceTo(v) + " ");
        System.out.println();
    }
}
