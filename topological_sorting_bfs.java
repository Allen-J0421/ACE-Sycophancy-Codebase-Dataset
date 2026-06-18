import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

class TopologicalSort {

    static List<Integer> topoSort(DirectedGraph graph) {
        int vertexCount = graph.vertexCount();
        int[] indegree = buildIndegreeTable(graph);
        Deque<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        enqueueZeroIndegreeVertices(indegree, queue);

        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            order.add(vertex);
            for (int next : graph.neighborsOf(vertex)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.add(next);
                }
            }
        }

        if (order.size() != vertexCount) {
            throw new IllegalStateException("Topological sort requires a directed acyclic graph.");
        }

        return order;
    }

    private static int[] buildIndegreeTable(DirectedGraph graph) {
        int[] indegree = new int[graph.vertexCount()];
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            for (int next : graph.neighborsOf(vertex)) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private static void enqueueZeroIndegreeVertices(int[] indegree, Deque<Integer> queue) {
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
    }

    private static DirectedGraph buildSampleGraph() {
        DirectedGraph graph = DirectedGraph.withVertexCount(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 1);
        graph.addEdge(5, 2);
        return graph;
    }

    private static String formatOrder(List<Integer> order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        DirectedGraph graph = buildSampleGraph();
        List<Integer> order = topoSort(graph);
        System.out.println(formatOrder(order));
    }

    private static final class DirectedGraph {
        private final List<List<Integer>> adjacencyList;

        private DirectedGraph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        static DirectedGraph withVertexCount(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count must be non-negative.");
            }

            List<List<Integer>> adjacencyList = new ArrayList<>();
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
            return new DirectedGraph(adjacencyList);
        }

        int vertexCount() {
            return adjacencyList.size();
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return adjacencyList.get(vertex);
        }

        void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);
            adjacencyList.get(source).add(destination);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
            }
        }
    }
}
