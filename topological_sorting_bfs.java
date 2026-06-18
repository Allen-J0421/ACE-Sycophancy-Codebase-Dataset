import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

class TopologicalSort {

    private TopologicalSort() {
        // Utility class.
    }

    static ArrayList<Integer> topoSort(ArrayList<ArrayList<Integer>> adj) {
        return new ArrayList<>(topologicalSort(adj));
    }

    static List<Integer> topologicalSort(List<? extends List<Integer>> graph) {
        validateGraph(graph);

        int[] indegree = computeIndegrees(graph);
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

    private static int[] computeIndegrees(List<? extends List<Integer>> graph) {
        int[] indegree = new int[graph.size()];
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            for (int neighbor : graph.get(vertex)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private static void validateGraph(List<? extends List<Integer>> graph) {
        Objects.requireNonNull(graph, "graph");

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            List<Integer> neighbors = Objects.requireNonNull(graph.get(vertex),
                    "graph[" + vertex + "]");
            for (int neighbor : neighbors) {
                if (neighbor < 0 || neighbor >= graph.size()) {
                    throw new IllegalArgumentException(
                            "Invalid edge from " + vertex + " to " + neighbor);
                }
            }
        }
    }

    private static void enqueueZeroIndegreeVertices(int[] indegree, Deque<Integer> queue) {
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.addLast(vertex);
            }
        }
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
    }

    private static ArrayList<ArrayList<Integer>> createSampleGraph(int vertices) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    public static void main(String[] args) {
        int n = 6;
        ArrayList<ArrayList<Integer>> adj = createSampleGraph(n);

        addEdge(adj, 0, 1);
        addEdge(adj, 1, 2);
        addEdge(adj, 2, 3);
        addEdge(adj, 4, 5);
        addEdge(adj, 5, 1);
        addEdge(adj, 5, 2);

        ArrayList<Integer> res = topoSort(adj);
        for (int vertex : res) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
