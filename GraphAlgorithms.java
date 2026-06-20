import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class GraphAlgorithms {
    private GraphAlgorithms() {
    }

    public static boolean hasCycle(DirectedGraphView graph) {
        Objects.requireNonNull(graph, "graph");
        return traverseTopologically(graph, vertex -> { }) != graph.vertexCount();
    }

    public static List<Integer> topologicalSort(DirectedGraphView graph) {
        Objects.requireNonNull(graph, "graph");

        List<Integer> topologicalOrder = new ArrayList<>(graph.vertexCount());
        traverseTopologically(graph, topologicalOrder::add);
        return Collections.unmodifiableList(topologicalOrder);
    }

    private static int traverseTopologically(DirectedGraphView graph, IntConsumer vertexConsumer) {
        int[] inDegree = computeInDegrees(graph);
        Deque<Integer> readyVertices = new ArrayDeque<>();
        enqueueZeroInDegreeVertices(inDegree, readyVertices);

        int processedVertexCount = 0;
        while (!readyVertices.isEmpty()) {
            int currentVertex = readyVertices.removeFirst();
            processedVertexCount++;
            vertexConsumer.accept(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (--inDegree[neighbor] == 0) {
                    readyVertices.addLast(neighbor);
                }
            }
        }

        return processedVertexCount;
    }

    private static int[] computeInDegrees(DirectedGraphView graph) {
        int vertexCount = graph.vertexCount();
        int[] inDegree = new int[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            for (int neighbor : graph.neighborsOf(vertex)) {
                inDegree[neighbor]++;
            }
        }

        return inDegree;
    }

    private static void enqueueZeroInDegreeVertices(int[] inDegree, Deque<Integer> queue) {
        for (int vertex = 0; vertex < inDegree.length; vertex++) {
            if (inDegree[vertex] == 0) {
                queue.addLast(vertex);
            }
        }
    }
}
