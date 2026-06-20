import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class TopologicalSorter {
    private TopologicalSorter() {
    }

    public static List<Integer> sort(DirectedGraphView graph) {
        Objects.requireNonNull(graph, "graph");

        int[] inDegree = computeInDegrees(graph);
        Deque<Integer> readyVertices = new ArrayDeque<>();
        enqueueZeroInDegreeVertices(inDegree, readyVertices);

        List<Integer> topologicalOrder = new ArrayList<>(graph.vertexCount());
        while (!readyVertices.isEmpty()) {
            int currentVertex = readyVertices.removeFirst();
            topologicalOrder.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (--inDegree[neighbor] == 0) {
                    readyVertices.addLast(neighbor);
                }
            }
        }

        return Collections.unmodifiableList(topologicalOrder);
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
