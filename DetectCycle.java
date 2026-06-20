import java.util.ArrayDeque;
import java.util.Deque;

public class DetectCycle {

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
        int[] inDegree = new int[graph.vertexCount()];

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
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

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.withVertexCount(4);

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);

        System.out.println(hasCycle(graph));
    }
}
