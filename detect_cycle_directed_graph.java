import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class DetectCycle {

    private static final int SAMPLE_VERTEX_COUNT = 4;
    private static final Edge[] SAMPLE_EDGES = {
            new Edge(0, 1),
            new Edge(1, 2),
            new Edge(2, 0),
            new Edge(2, 3)
    };

    private DetectCycle() {
    }

    static boolean isCyclic(List<? extends List<Integer>> adjacencyList) {
        return containsCycle(adjacencyList);
    }

    static boolean containsCycle(List<? extends List<Integer>> adjacencyList) {
        return hasCycle(adjacencyList);
    }

    private static boolean hasCycle(List<? extends List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] inDegrees = calculateInDegrees(adjacencyList, vertexCount);
        int visitedCount = countVerticesInTopologicalOrder(adjacencyList, inDegrees);

        return visitedCount != vertexCount;
    }

    private static int countVerticesInTopologicalOrder(
            List<? extends List<Integer>> adjacencyList,
            int[] inDegrees
    ) {
        Deque<Integer> zeroInDegreeVertices = findZeroInDegreeVertices(inDegrees);
        int visitedCount = 0;

        while (!zeroInDegreeVertices.isEmpty()) {
            int vertex = zeroInDegreeVertices.removeFirst();
            visitedCount++;

            for (int neighbor : adjacencyList.get(vertex)) {
                decrementInDegree(neighbor, inDegrees, zeroInDegreeVertices);
            }
        }

        return visitedCount;
    }

    private static void decrementInDegree(
            int vertex,
            int[] inDegrees,
            Deque<Integer> zeroInDegreeVertices
    ) {
        inDegrees[vertex]--;
        if (inDegrees[vertex] == 0) {
            zeroInDegreeVertices.addLast(vertex);
        }
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

    private static DirectedGraph buildSampleGraph() {
        DirectedGraph graph = new DirectedGraph(SAMPLE_VERTEX_COUNT);
        for (Edge edge : SAMPLE_EDGES) {
            graph.addEdge(edge);
        }
        return graph;
    }

    public static void main(String[] args) {
        DirectedGraph graph = buildSampleGraph();
        System.out.println(Boolean.toString(containsCycle(graph.adjacencyList())));
    }

    private static final class DirectedGraph {

        private final List<List<Integer>> adjacencyList;

        private DirectedGraph(int vertexCount) {
            this.adjacencyList = new ArrayList<>();
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        private void addEdge(Edge edge) {
            DetectCycle.addEdge(adjacencyList, edge.source, edge.destination);
        }

        private List<List<Integer>> adjacencyList() {
            return adjacencyList;
        }
    }

    private static final class Edge {

        private final int source;
        private final int destination;

        private Edge(int source, int destination) {
            this.source = source;
            this.destination = destination;
        }
    }
}
