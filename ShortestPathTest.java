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
        List<Integer> distances = ShortestPath.shortestPaths(GraphFixtures.weightedUndirectedExample(), 0);
        assertDistances("sample graph", distances, 0, 4, 7, 9, 10);
    }

    private static void verifiesBuilderChainingBuildsGraph() {
        List<Integer> distances = ShortestPath.shortestPaths(GraphFixtures.threeVertexChain(), 0);
        assertDistances("builder chaining", distances, 0, 2, 3);
    }

    private static void verifiesUnreachableVerticesRemainInfinite() {
        List<Integer> distances = ShortestPath.shortestPaths(GraphFixtures.threeVertexGraphWithUnreachableTail(), 0);
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
