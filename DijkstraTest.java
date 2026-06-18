import java.util.ArrayList;
import java.util.List;

public final class DijkstraTest {
    private DijkstraTest() {
    }

    public static void main(String[] args) {
        verifiesSampleGraphDistances();
        verifiesLegacyAdapterSupportsShortestPaths();
        verifiesUnreachableVerticesRemainInfinite();
        System.out.println("All Dijkstra tests passed");
    }

    private static void verifiesSampleGraphDistances() {
        List<Integer> distances = Dijkstra.shortestPaths(SampleGraphs.weightedUndirectedExample(), 0);
        assertDistances("sample graph", distances, 0, 4, 7, 9, 10);
    }

    private static void verifiesLegacyAdapterSupportsShortestPaths() {
        ArrayList<ArrayList<int[]>> legacy = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            legacy.add(new ArrayList<>());
        }
        Dijkstra.addEdge(legacy, 0, 1, 2);
        Dijkstra.addEdge(legacy, 1, 2, 1);

        List<Integer> distances = Dijkstra.dijkstra(legacy, 0);
        assertDistances("legacy adapter", distances, 0, 2, 3);
    }

    private static void verifiesUnreachableVerticesRemainInfinite() {
        Graph graph = GraphBuilder.withVertexCount(3)
            .addUndirectedEdge(0, 1, 5)
            .build();

        List<Integer> distances = Dijkstra.shortestPaths(graph, 0);
        assertDistances("unreachable vertex", distances, 0, 5, Integer.MAX_VALUE);
    }

    private static void assertDistances(String label, List<Integer> actual, int... expected) {
        List<Integer> expectedDistances = new ArrayList<>(expected.length);
        for (int value : expected) {
            expectedDistances.add(value);
        }

        if (!actual.equals(expectedDistances)) {
            throw new AssertionError(
                label + " distances mismatch. expected=" + expectedDistances + " actual=" + actual
            );
        }
    }
}
