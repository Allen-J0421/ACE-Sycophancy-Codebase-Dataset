import java.util.ArrayList;
import java.util.List;

public final class ShortestPathTest {
    private ShortestPathTest() {
    }

    public static void main(String[] args) {
        verifiesSampleGraphDistances();
        verifiesBuilderChainingBuildsGraph();
        verifiesUnreachableVerticesRemainInfinite();
        System.out.println("All ShortestPath tests passed");
    }

    private static void verifiesSampleGraphDistances() {
        List<Integer> distances = ShortestPath.shortestPaths(SampleGraphs.weightedUndirectedExample(), 0);
        assertDistances("sample graph", distances, 0, 4, 7, 9, 10);
    }

    private static void verifiesBuilderChainingBuildsGraph() {
        Graph graph = GraphBuilder.withVertexCount(3)
            .addUndirectedEdge(0, 1, 2)
            .addUndirectedEdge(1, 2, 1)
            .build();

        List<Integer> distances = ShortestPath.shortestPaths(graph, 0);
        assertDistances("builder chaining", distances, 0, 2, 3);
    }

    private static void verifiesUnreachableVerticesRemainInfinite() {
        Graph graph = GraphBuilder.withVertexCount(3)
            .addUndirectedEdge(0, 1, 5)
            .build();

        List<Integer> distances = ShortestPath.shortestPaths(graph, 0);
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
