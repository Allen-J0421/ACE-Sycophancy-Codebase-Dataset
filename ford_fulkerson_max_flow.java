import java.util.*;

class MaxFlow {

    private static int[] bfs(int[][] residualGraph, int source, int sink) {
        int n = residualGraph.length;
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new LinkedList<>();
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

    private static int[][] copyGraph(int[][] graph) {
        return Arrays.stream(graph).map(int[]::clone).toArray(int[][]::new);
    }

    private static int findBottleneck(int[][] residualGraph, int source, int sink, int[] parent) {
        int bottleneck = Integer.MAX_VALUE;
        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            bottleneck = Math.min(bottleneck, residualGraph[u][v]);
        }
        return bottleneck;
    }

    private static void augmentPath(int[][] residualGraph, int source, int sink, int[] parent, int flow) {
        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            residualGraph[u][v] -= flow;
            residualGraph[v][u] += flow;
        }
    }

    public int fordFulkerson(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0)
            throw new IllegalArgumentException("Graph must be non-null and non-empty");
        int n = graph.length;
        for (int i = 0; i < n; i++)
            if (graph[i] == null || graph[i].length != n)
                throw new IllegalArgumentException("Graph must be a square matrix");
        if (source < 0 || source >= n || sink < 0 || sink >= n)
            throw new IllegalArgumentException("Source and sink must be valid vertex indices");
        if (source == sink)
            throw new IllegalArgumentException("Source and sink must be distinct");

        int[][] residualGraph = copyGraph(graph);
        int maxFlow = 0;
        int[] parent;

        while ((parent = bfs(residualGraph, source, sink)) != null) {
            int pathFlow = findBottleneck(residualGraph, source, sink, parent);
            augmentPath(residualGraph, source, sink, parent, pathFlow);
            maxFlow += pathFlow;
        }

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

        MaxFlow m = new MaxFlow();
        System.out.println("The maximum possible flow is " + m.fordFulkerson(graph, 0, 5));
    }
}
