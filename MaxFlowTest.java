import java.util.Arrays;

final class MaxFlowTest {
    private MaxFlowTest() {
    }

    public static void main(String[] args) {
        returnsExpectedSampleFlow();
        doesNotMutateInputGraph();
        returnsZeroWhenNoPathExists();
        rejectsInvalidGraphs();
    }

    private static void returnsExpectedSampleFlow() {
        assertEquals(
            SampleNetworks.SIX_VERTEX_MAX_FLOW,
            new MaxFlow().maximumFlow(
                SampleNetworks.sixVertexNetwork(),
                SampleNetworks.SIX_VERTEX_SOURCE,
                SampleNetworks.SIX_VERTEX_SINK
            )
        );
    }

    private static void doesNotMutateInputGraph() {
        int[][] graph = SampleNetworks.sixVertexNetwork();
        int[][] original = copyGraph(graph);

        new MaxFlow().maximumFlow(
            graph,
            SampleNetworks.SIX_VERTEX_SOURCE,
            SampleNetworks.SIX_VERTEX_SINK
        );

        if (!Arrays.deepEquals(original, graph)) {
            throw new AssertionError("Expected input graph to remain unchanged.");
        }
    }

    private static void returnsZeroWhenNoPathExists() {
        int[][] graph = new int[][] {
            { 0, 0, 0 },
            { 0, 0, 4 },
            { 0, 0, 0 }
        };

        assertEquals(0, new MaxFlow().maximumFlow(graph, 0, 2));
    }

    private static void rejectsInvalidGraphs() {
        assertThrows(() -> new MaxFlow().maximumFlow(null, 0, 1));
        assertThrows(() -> new MaxFlow().maximumFlow(new int[][] { { 0, 1 } }, 0, 1));
        assertThrows(() -> new MaxFlow().maximumFlow(new int[][] { { 0, -1 }, { 0, 0 } }, 0, 1));
        assertThrows(() -> new MaxFlow().maximumFlow(new int[][] { { 0 } }, 0, 0));
    }

    private static int[][] copyGraph(int[][] graph) {
        int[][] copy = new int[graph.length][];

        for (int row = 0; row < graph.length; row++) {
            copy[row] = Arrays.copyOf(graph[row], graph[row].length);
        }

        return copy;
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual + ".");
        }
    }

    private static void assertThrows(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException.");
    }
}
