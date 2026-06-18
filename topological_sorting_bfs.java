import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

final class TopologicalSort {

    private TopologicalSort() {
        // Utility class.
    }

    static ArrayList<Integer> topoSort(ArrayList<ArrayList<Integer>> adj) {
        return topologicalSort(adj);
    }

    static ArrayList<Integer> topologicalSort(List<? extends List<Integer>> graph) {
        int[] indegree = buildIndegrees(graph);
        Deque<Integer> queue = new ArrayDeque<>();
        ArrayList<Integer> order = new ArrayList<>(graph.size());

        enqueueZeroIndegreeVertices(indegree, queue);

        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            order.add(vertex);

            for (int neighbor : graph.get(vertex)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.addLast(neighbor);
                }
            }
        }

        if (order.size() != graph.size()) {
            throw new IllegalArgumentException("Graph contains a cycle.");
        }

        return order;
    }

    private static int[] buildIndegrees(List<? extends List<Integer>> graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.size();
        int[] indegree = new int[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = Objects.requireNonNull(graph.get(vertex),
                    "graph[" + vertex + "]");
            for (int neighbor : neighbors) {
                if (neighbor < 0 || neighbor >= vertexCount) {
                    throw new IllegalArgumentException(
                            "Invalid edge from " + vertex + " to " + neighbor);
                }
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
    }

    private static ArrayList<ArrayList<Integer>> newGraph(int vertices) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void enqueueZeroIndegreeVertices(int[] indegree, Deque<Integer> queue) {
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.addLast(vertex);
            }
        }
    }

    private static void buildSampleGraph(ArrayList<ArrayList<Integer>> graph) {
        addEdge(graph, 0, 1);
        addEdge(graph, 1, 2);
        addEdge(graph, 2, 3);
        addEdge(graph, 4, 5);
        addEdge(graph, 5, 1);
        addEdge(graph, 5, 2);
    }

    private static void printOrder(List<Integer> order) {
        for (int vertex : order) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> graph = newGraph(6);
        buildSampleGraph(graph);
        printOrder(topoSort(graph));
    }
}
