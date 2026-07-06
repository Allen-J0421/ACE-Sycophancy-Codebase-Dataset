import java.util.Arrays;

final class BellmanFordDemo {

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.from(5, new int[][] {
            {0, 1, 5},
            {1, 2, 1},
            {1, 3, 2},
            {2, 4, 1},
            {4, 3, -1}
        });

        BellmanFord.shortestPaths(graph, 0).accept(ResultVisitor.of(
            d -> {
                System.out.println(Arrays.toString(d.all()));
                return null;
            },
            nc -> {
                System.out.println("Negative cycle detected: " + nc.vertices());
                return null;
            }
        ));
    }
}
