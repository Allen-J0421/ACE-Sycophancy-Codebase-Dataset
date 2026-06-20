import java.util.Arrays;

final class BellmanFordDemo {
    private BellmanFordDemo() {
    }

    public static void main(String[] args) {
        BellmanFord.Graph graph = BellmanFord.Graph.of(
            5,
            BellmanFord.Edge.of(1, 3, 2),
            BellmanFord.Edge.of(4, 3, -1),
            BellmanFord.Edge.of(2, 4, 1),
            BellmanFord.Edge.of(1, 2, 1),
            BellmanFord.Edge.of(0, 1, 5)
        );
        int source = 0;

        BellmanFord.Result result = BellmanFord.computeShortestPaths(graph, source);
        if (result.hasNegativeCycle()) {
            System.out.println("Negative cycle detected");
            return;
        }

        System.out.println(Arrays.toString(result.distances()));
    }
}
