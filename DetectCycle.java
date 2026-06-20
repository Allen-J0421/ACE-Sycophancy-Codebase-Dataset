import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DetectCycle {

    public static boolean isCyclic(List<? extends List<Integer>> adjacencyList) {
        int[] inDegree = computeInDegrees(adjacencyList);
        Deque<Integer> zeroInDegreeVertices = new ArrayDeque<>();

        enqueueZeroInDegreeVertices(inDegree, zeroInDegreeVertices);

        int processedVertices = 0;
        while (!zeroInDegreeVertices.isEmpty()) {
            int currentVertex = zeroInDegreeVertices.removeFirst();
            processedVertices++;

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (--inDegree[neighbor] == 0) {
                    zeroInDegreeVertices.addLast(neighbor);
                }
            }
        }

        return processedVertices != adjacencyList.size();
    }

    private static int[] computeInDegrees(List<? extends List<Integer>> adjacencyList) {
        int[] inDegree = new int[adjacencyList.size()];

        for (List<Integer> neighbors : adjacencyList) {
            for (int neighbor : neighbors) {
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

    private static void addEdge(List<List<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = createGraph(4);

        addEdge(adjacencyList, 0, 1);
        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 2, 0);
        addEdge(adjacencyList, 2, 3);

        System.out.println(isCyclic(adjacencyList));
    }
}
