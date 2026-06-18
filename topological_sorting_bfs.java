import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class TopologicalSort {

    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final Edge[] SAMPLE_EDGES = {
        edge(0, 1),
        edge(1, 2),
        edge(2, 3),
        edge(4, 5),
        edge(5, 1),
        edge(5, 2)
    };

    private TopologicalSort() {
    }

    private static final class Edge {
        private final int from;
        private final int to;

        private Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    static List<Integer> topoSort(List<? extends List<Integer>> adjacencyList) {
        return topologicalSort(adjacencyList);
    }

    static List<Integer> topologicalSort(List<? extends List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] inDegree = calculateInDegrees(adjacencyList);
        Deque<Integer> readyVertices = collectZeroInDegreeVertices(inDegree);
        List<Integer> sortedVertices = new ArrayList<>(vertexCount);

        while (!readyVertices.isEmpty()) {
            int vertex = readyVertices.removeFirst();
            sortedVertices.add(vertex);
            enqueueReadyNeighbors(adjacencyList.get(vertex), inDegree, readyVertices);
        }

        return sortedVertices;
    }

    static void addEdge(List<? extends List<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private static void addEdge(List<? extends List<Integer>> adjacencyList, Edge edge) {
        addEdge(adjacencyList, edge.from, edge.to);
    }

    private static Edge edge(int from, int to) {
        return new Edge(from, to);
    }

    private static List<List<Integer>> buildSampleGraph() {
        return buildGraph(SAMPLE_VERTEX_COUNT, SAMPLE_EDGES);
    }

    private static List<List<Integer>> buildGraph(int vertexCount, Edge[] edges) {
        List<List<Integer>> adjacencyList = createEmptyAdjacencyList(vertexCount);
        for (Edge edge : edges) {
            addEdge(adjacencyList, edge);
        }
        return adjacencyList;
    }

    private static int[] calculateInDegrees(List<? extends List<Integer>> adjacencyList) {
        int[] inDegree = new int[adjacencyList.size()];
        for (List<Integer> neighbors : adjacencyList) {
            for (int neighbor : neighbors) {
                inDegree[neighbor]++;
            }
        }
        return inDegree;
    }

    private static Deque<Integer> collectZeroInDegreeVertices(int[] inDegree) {
        Deque<Integer> readyVertices = new ArrayDeque<>();
        for (int vertex = 0; vertex < inDegree.length; vertex++) {
            if (inDegree[vertex] == 0) {
                readyVertices.addLast(vertex);
            }
        }
        return readyVertices;
    }

    private static void enqueueReadyNeighbors(
            List<Integer> neighbors,
            int[] inDegree,
            Deque<Integer> readyVertices
    ) {
        for (int neighbor : neighbors) {
            inDegree[neighbor]--;
            if (inDegree[neighbor] == 0) {
                readyVertices.addLast(neighbor);
            }
        }
    }

    private static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    private static void printVertices(List<Integer> vertices) {
        System.out.println(formatVertices(vertices));
    }

    private static String formatVertices(List<Integer> vertices) {
        StringBuilder output = new StringBuilder();
        for (int vertex : vertices) {
            output.append(vertex).append(' ');
        }
        return output.toString();
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = buildSampleGraph();
        List<Integer> sortedVertices = topologicalSort(adjacencyList);
        printVertices(sortedVertices);
    }
}
