import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

class MaxFlow {

    private MaxFlow() {}

    private record Edge(int from, int to) {}

    public static int fordFulkerson(int[][] graph, int source, int sink) {
        validateGraph(graph, source, sink);

        int[][] residualGraph = copyGraph(graph);
        int maxFlow = 0;
        int[] parent;

        while ((parent = bfs(residualGraph, source, sink)) != null)
            maxFlow += augment(residualGraph, tracePath(parent, source, sink));

        return maxFlow;
    }

    public static void main(String[] args) {
        int[][] graph = {
            { 0, 16, 13,  0,  0,  0 },
            { 0,  0, 10, 12,  0,  0 },
            { 0,  4,  0,  0, 14,  0 },
            { 0,  0,  9,  0,  0, 20 },
            { 0,  0,  0,  7,  0,  4 },
            { 0,  0,  0,  0,  0,  0 }
        };

        System.out.println("The maximum possible flow is " + fordFulkerson(graph, 0, 5));
    }

    private static void validateGraph(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0)
            throw new IllegalArgumentException("Graph must be non-null and non-empty");
        int n = graph.length;
        for (int i = 0; i < n; i++)
            if (graph[i] == null || graph[i].length != n)
                throw new IllegalArgumentException("Graph must be a square matrix");
        if (source < 0 || source >= n)
            throw new IllegalArgumentException(
                "Source vertex " + source + " out of range [0, " + n + ")");
        if (sink < 0 || sink >= n)
            throw new IllegalArgumentException(
                "Sink vertex " + sink + " out of range [0, " + n + ")");
        if (source == sink)
            throw new IllegalArgumentException(
                "Source and sink must be distinct (both are " + source + ")");
    }

    private static int[][] copyGraph(int[][] graph) {
        return Arrays.stream(graph).map(int[]::clone).toArray(int[][]::new);
    }

    private static int[] bfs(int[][] residualGraph, int source, int sink) {
        int n = residualGraph.length;
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < n; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    parent[v] = u;
                    if (v == sink) return parent;
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }

        return null;
    }

    private static List<Edge> tracePath(int[] parent, int source, int sink) {
        List<Edge> edges = new ArrayList<>();
        for (int v = sink; v != source; v = parent[v])
            edges.add(new Edge(parent[v], v));
        return edges;
    }

    private static int augment(int[][] residualGraph, List<Edge> path) {
        int flow = Integer.MAX_VALUE;
        for (Edge edge : path)
            flow = Math.min(flow, residualGraph[edge.from()][edge.to()]);
        for (Edge edge : path) {
            residualGraph[edge.from()][edge.to()] -= flow;
            residualGraph[edge.to()][edge.from()] += flow;
        }
        return flow;
    }
}
