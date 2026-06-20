import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public final class DetectCycleDirectedGraph {

    private DetectCycleDirectedGraph() {
    }

    public static boolean hasCycle(DirectedGraph graph) {
        return countVisitedVertices(graph) != graph.vertexCount();
    }

    public static boolean hasCycle(List<List<Integer>> adjacencyList) {
        return hasCycle(DirectedGraph.fromAdjacencyList(adjacencyList));
    }

    private static int countVisitedVertices(DirectedGraph graph) {
        int[] indegree = computeIndegree(graph);
        Queue<Integer> zeroIndegreeVertices = collectZeroIndegreeVertices(indegree);
        int visitedCount = 0;

        while (!zeroIndegreeVertices.isEmpty()) {
            int vertex = zeroIndegreeVertices.poll();
            visitedCount++;

            for (int neighbor : graph.neighborsOf(vertex)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    zeroIndegreeVertices.add(neighbor);
                }
            }
        }

        return visitedCount;
    }

    private static int[] computeIndegree(DirectedGraph graph) {
        int[] indegree = new int[graph.vertexCount()];

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            List<Integer> neighbors = graph.neighborsOf(vertex);
            for (int neighbor : neighbors) {
                indegree[neighbor]++;
            }
        }

        return indegree;
    }

    private static Queue<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
        return queue;
    }

    private static DirectedGraph createSampleGraph() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        return graph;
    }

    public static void main(String[] args) {
        DirectedGraph graph = createSampleGraph();
        System.out.println(hasCycle(graph));
    }

    public static final class DirectedGraph {
        private final List<List<Integer>> adjacencyList;

        public DirectedGraph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        public static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
            DirectedGraph graph = new DirectedGraph(adjacencyList.size());
            for (int source = 0; source < adjacencyList.size(); source++) {
                for (int destination : adjacencyList.get(source)) {
                    graph.addEdge(source, destination);
                }
            }
            return graph;
        }

        public void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);
            adjacencyList.get(source).add(destination);
        }

        public int vertexCount() {
            return adjacencyList.size();
        }

        public List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return Collections.unmodifiableList(adjacencyList.get(vertex));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IndexOutOfBoundsException("Invalid vertex: " + vertex);
            }
        }
    }
}
