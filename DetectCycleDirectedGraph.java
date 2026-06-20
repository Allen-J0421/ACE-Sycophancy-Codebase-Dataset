import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class DetectCycleDirectedGraph {

    private DetectCycleDirectedGraph() {
    }

    public static boolean hasCycle(List<List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] indegree = computeIndegree(adjacencyList);
        Queue<Integer> zeroIndegreeVertices = new ArrayDeque<>();
        int visitedCount = 0;

        enqueueZeroIndegreeVertices(indegree, zeroIndegreeVertices);

        while (!zeroIndegreeVertices.isEmpty()) {
            int vertex = zeroIndegreeVertices.poll();
            visitedCount++;

            for (int neighbor : adjacencyList.get(vertex)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    zeroIndegreeVertices.add(neighbor);
                }
            }
        }

        return visitedCount != vertexCount;
    }

    public static void addEdge(List<List<Integer>> adjacencyList, int source, int destination) {
        adjacencyList.get(source).add(destination);
    }

    public static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    private static int[] computeIndegree(List<List<Integer>> adjacencyList) {
        int[] indegree = new int[adjacencyList.size()];

        for (List<Integer> neighbors : adjacencyList) {
            for (int neighbor : neighbors) {
                indegree[neighbor]++;
            }
        }

        return indegree;
    }

    private static void enqueueZeroIndegreeVertices(int[] indegree, Queue<Integer> queue) {
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = createGraph(4);

        addEdge(adjacencyList, 0, 1);
        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 2, 0);
        addEdge(adjacencyList, 2, 3);

        System.out.println(hasCycle(adjacencyList));
    }
}
