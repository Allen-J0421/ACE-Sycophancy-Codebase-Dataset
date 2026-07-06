final class BellmanFordDemo {

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.from(5, new int[][] {
            {0, 1, 5},
            {1, 2, 1},
            {1, 3, 2},
            {2, 4, 1},
            {4, 3, -1}
        });

        ShortestPathResult result = BellmanFord.shortestPaths(graph, 0);

        if (result instanceof Distances d) {
            for (int dist : d.all()) {
                System.out.print(dist + " ");
            }
            System.out.println();
        } else if (result instanceof NegativeCycle nc) {
            System.out.println("Negative cycle detected: " + nc.vertices());
        }
    }
}
