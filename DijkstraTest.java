import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DijkstraTest {
    public static void main(String[] args) {
        findsShortestPathsOnWeightedGraph();
        preservesLegacyAdjacencyApi();
        reportsUnreachableVertices();

        System.out.println("All tests passed.");
    }

    private static void findsShortestPathsOnWeightedGraph() {
        assertDistances(
                Arrays.asList(0, 4, 7, 9, 10),
                Dijkstra.shortestPathsFrom(SampleGraphs.demoGraph(), 0));
    }

    private static void preservesLegacyAdjacencyApi() {
        ArrayList<ArrayList<int[]>> adjacency = emptyLegacyGraph(5);

        Dijkstra.addEdge(adjacency, 0, 1, 4);
        Dijkstra.addEdge(adjacency, 0, 2, 8);
        Dijkstra.addEdge(adjacency, 1, 4, 6);
        Dijkstra.addEdge(adjacency, 1, 2, 3);
        Dijkstra.addEdge(adjacency, 2, 3, 2);
        Dijkstra.addEdge(adjacency, 3, 4, 10);

        assertDistances(Arrays.asList(0, 4, 7, 9, 10), Dijkstra.dijkstra(adjacency, 0));
    }

    private static void reportsUnreachableVertices() {
        WeightedGraph graph = WeightedGraph.builder(3)
                .addUndirectedEdge(0, 1, 5)
                .build();

        assertDistances(
                Arrays.asList(0, 5, Integer.MAX_VALUE),
                Dijkstra.shortestPathsFrom(graph, 0));
    }

    private static ArrayList<ArrayList<int[]>> emptyLegacyGraph(int vertexCount) {
        ArrayList<ArrayList<int[]>> adjacency = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        return adjacency;
    }

    private static void assertDistances(List<Integer> expected, List<Integer> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
