import java.util.ArrayDeque;
import java.util.Deque;

public final class CycleDetector {
    private CycleDetector() {
    }

    public static boolean hasCycle(DirectedGraph graph) {
        int[] inDegree = computeInDegrees(graph);
        Deque<Integer> readyVertices = new ArrayDeque<>();

        enqueueZeroInDegreeVertices(inDegree, readyVertices);

        int processedVertexCount = 0;
        while (!readyVertices.isEmpty()) {
            int currentVertex = readyVertices.removeFirst();
            processedVertexCount++;

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (--inDegree[neighbor] == 0) {
                    readyVertices.addLast(neighbor);
                }
            }
        }

        return processedVertexCount != graph.vertexCount();
    }

    private static int[] computeInDegrees(DirectedGraph graph) {
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
