import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class DetectCycle {

    private static final int SAMPLE_VERTEX_COUNT = 4;
    private static final int[][] SAMPLE_EDGES = {
            {0, 1},
            {1, 2},
            {2, 0},
            {2, 3}
    };

    private DetectCycle() {
    }

    static boolean isCyclic(List<? extends List<Integer>> adjacencyList) {
        return hasCycle(adjacencyList);
    }

    private static boolean hasCycle(List<? extends List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] inDegrees = calculateInDegrees(adjacencyList, vertexCount);
        Deque<Integer> zeroInDegreeVertices = findZeroInDegreeVertices(inDegrees);

        int visitedCount = 0;

        while (!zeroInDegreeVertices.isEmpty()) {
            int vertex = zeroInDegreeVertices.removeFirst();
            visitedCount++;

            for (int neighbor : adjacencyList.get(vertex)) {
                inDegrees[neighbor]--;
                if (inDegrees[neighbor] == 0) {
                    zeroInDegreeVertices.addLast(neighbor);
                }
            }
        }

        return visitedCount != vertexCount;
    }

    private static int[] calculateInDegrees(
            List<? extends List<Integer>> adjacencyList,
            int vertexCount
    ) {
        int[] inDegrees = new int[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            for (int neighbor : adjacencyList.get(vertex)) {
                validateVertex(neighbor, vertexCount);
                inDegrees[neighbor]++;
            }
        }

        return inDegrees;
    }

    private static Deque<Integer> findZeroInDegreeVertices(int[] inDegrees) {
        Deque<Integer> vertices = new ArrayDeque<>();

        for (int vertex = 0; vertex < inDegrees.length; vertex++) {
            if (inDegrees[vertex] == 0) {
                vertices.addLast(vertex);
            }
        }

        return vertices;
    }

    static void addEdge(List<? extends List<Integer>> adjacencyList, int source, int destination) {
        validateVertex(source, adjacencyList.size());
        validateVertex(destination, adjacencyList.size());
        adjacencyList.get(source).add(destination);
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }

    private static List<List<Integer>> buildSampleGraph() {
        List<List<Integer>> adjacencyList = createGraph(SAMPLE_VERTEX_COUNT);
        for (int[] edge : SAMPLE_EDGES) {
            addEdge(adjacencyList, edge[0], edge[1]);
        }
        return adjacencyList;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = buildSampleGraph();
        System.out.println(isCyclic(adjacencyList) ? "true" : "false");
    }
}
