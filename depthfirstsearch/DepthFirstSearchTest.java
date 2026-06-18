package depthfirstsearch;

import java.util.List;

import depthfirstsearch.graph.Graph;
import depthfirstsearch.graph.GraphBuilder;
import depthfirstsearch.search.DepthFirstSearch;

public final class DepthFirstSearchTest {

    private DepthFirstSearchTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldTraverseEveryComponent();
        shouldTraverseSingleComponent();
        shouldTraverseIntoCallback();
        shouldRejectInvalidStartVertex();
        shouldExposeImmutableNeighbors();
        shouldRejectMalformedEdgeList();
    }

    private static void shouldTraverseEveryComponent() {
        Graph graph = sampleGraph();

        assertEquals(List.of(0, 3, 2, 1, 4, 5), DepthFirstSearch.traverse(graph));
    }

    private static void shouldTraverseSingleComponent() {
        Graph graph = sampleGraph();

        assertEquals(List.of(5, 4), DepthFirstSearch.traverseFrom(graph, 5));
    }

    private static void shouldTraverseIntoCallback() {
        Graph graph = sampleGraph();
        var order = new java.util.ArrayList<Integer>();

        DepthFirstSearch.traverseFrom(graph, 5, order::add);

        assertEquals(List.of(5, 4), order);
    }

    private static void shouldRejectInvalidStartVertex() {
        Graph graph = sampleGraph();

        try {
            DepthFirstSearch.traverseFrom(graph, 7);
            throw new AssertionError("Expected invalid start vertex to fail");
        } catch (IndexOutOfBoundsException expected) {
            // Expected.
        }
    }

    private static void shouldExposeImmutableNeighbors() {
        Graph graph = sampleGraph();

        try {
            graph.neighbors(0).add(99);
            throw new AssertionError("Expected neighbors to be immutable");
        } catch (UnsupportedOperationException expected) {
            // Expected.
        }
    }

    private static void shouldRejectMalformedEdgeList() {
        try {
            Graph.fromUndirectedEdges(3, new int[][] {{0, 1, 2}});
            throw new AssertionError("Expected malformed edge list to fail");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static void assertEquals(List<Integer> expected, List<Integer> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static Graph sampleGraph() {
        return new GraphBuilder(6)
                .addUndirectedEdge(1, 2)
                .addUndirectedEdge(0, 3)
                .addUndirectedEdge(2, 0)
                .addUndirectedEdge(5, 4)
                .build();
    }
}
