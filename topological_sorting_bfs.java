import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class TopologicalSort {

    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final int[][] SAMPLE_EDGES = {
        {0, 1},
        {1, 2},
        {2, 3},
        {4, 5},
        {5, 1},
        {5, 2}
    };

    private TopologicalSort() {
    }

    static List<Integer> topoSort(List<? extends List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] indegree = calculateIndegrees(adjacencyList, vertexCount);
        Queue<Integer> readyVertices = collectZeroIndegreeVertices(indegree);
        List<Integer> sortedVertices = new ArrayList<>(vertexCount);

        while (!readyVertices.isEmpty()) {
            int vertex = readyVertices.poll();
            sortedVertices.add(vertex);
            for (int next : adjacencyList.get(vertex)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    readyVertices.add(next);
                }
            }
        }

        return sortedVertices;
    }

    static void addEdge(List<? extends List<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private static List<List<Integer>> buildGraph(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = createGraph(vertexCount);
        for (int[] edge : edges) {
            addEdge(adjacencyList, edge[0], edge[1]);
        }
        return adjacencyList;
    }

    private static int[] calculateIndegrees(List<? extends List<Integer>> adjacencyList, int vertexCount) {
        int[] indegree = new int[vertexCount];
        for (List<Integer> neighbors : adjacencyList) {
            for (int next : neighbors) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private static Queue<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Queue<Integer> readyVertices = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                readyVertices.add(vertex);
            }
        }
        return readyVertices;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    private static void printVertices(List<Integer> vertices) {
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = buildGraph(SAMPLE_VERTEX_COUNT, SAMPLE_EDGES);
        List<Integer> sortedVertices = topoSort(adjacencyList);
        printVertices(sortedVertices);
    }
}
