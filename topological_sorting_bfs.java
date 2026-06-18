import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

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

    static <T extends List<Integer>> void addEdge(List<T> adj, int u, int v) {
        adj.get(u).add(v);
    }

    private static ArrayList<List<Integer>> newGraph(int vertices) {
        ArrayList<List<Integer>> graph = new ArrayList<>(vertices);
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

    private static void buildSampleGraph(List<List<Integer>> graph) {
        for (int[] edge : SAMPLE_EDGES) {
            addEdge(graph, edge[0], edge[1]);
        }
    }

    private static void printOrder(List<Integer> order) {
        for (int vertex : order) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        List<List<Integer>> graph = newGraph(SAMPLE_VERTEX_COUNT);
        buildSampleGraph(graph);
        printOrder(topologicalSort(graph));
    }
}
