/** Runnable demonstration of {@link BellmanFord} on a small sample graph. */
final class BellmanFordDemo {

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.from(5, new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        });

        ShortestPathResult result = BellmanFord.shortestPaths(graph, 0);

        switch (result) {
            case NegativeCycle cycle ->
                System.out.println("Graph contains a negative-weight cycle: " + cycle.vertices());
            case Distances distances -> {
                for (int v = 0; v < distances.vertexCount(); v++) {
                    System.out.print(
                        (distances.isReachable(v) ? distances.distanceTo(v) : "INF") + " ");
                }
                System.out.println();

                int target = 3;
                Path path = distances.pathTo(target);
                System.out.println("Shortest path " + distances.source() + " -> " + target
                    + ": " + path.vertices() + " (weight " + path.totalWeight() + ")");
            }
        }
    }
}
