import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DepthFirstSearchTest {

    private DepthFirstSearchTest() {
    }

    public static void main(String[] args) {
        traversesDisconnectedGraph();
        preservesNeighborOrderWithCrossEdges();
        rejectsNullAdjacencyList();
        rejectsNullNeighborList();
        rejectsNullNeighbor();
        rejectsOutOfRangeNeighbor();
    }

    private static void traversesDisconnectedGraph() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        adjacencyList.add(Collections.emptyList());
        adjacencyList.add(Collections.singletonList(2));
        adjacencyList.add(Collections.emptyList());

        assertTraversalEquals(Arrays.asList(0, 1, 2), adjacencyList);
    }

    private static void preservesNeighborOrderWithCrossEdges() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        adjacencyList.add(Arrays.asList(1, 2));
        adjacencyList.add(Arrays.asList(2, 3));
        adjacencyList.add(Collections.emptyList());
        adjacencyList.add(Collections.emptyList());

        assertTraversalEquals(Arrays.asList(0, 1, 2, 3), adjacencyList);
    }

    private static void rejectsNullAdjacencyList() {
        assertThrowsIllegalArgument(() -> DepthFirstSearch.dfs(null));
    }

    private static void rejectsNullNeighborList() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        adjacencyList.add(null);

        assertThrowsIllegalArgument(() -> DepthFirstSearch.dfs(adjacencyList));
    }

    private static void rejectsNullNeighbor() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        adjacencyList.add(Arrays.asList((Integer) null));

        assertThrowsIllegalArgument(() -> DepthFirstSearch.dfs(adjacencyList));
    }

    private static void rejectsOutOfRangeNeighbor() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        adjacencyList.add(Collections.singletonList(1));

        assertThrowsIllegalArgument(() -> DepthFirstSearch.dfs(adjacencyList));
    }

    private static void assertTraversalEquals(List<Integer> expected, List<List<Integer>> adjacencyList) {
        List<Integer> actual = DepthFirstSearch.dfs(adjacencyList);

        if (!expected.equals(actual)) {
            throw new AssertionError("Expected traversal " + expected + " but got " + actual);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException");
    }
}
