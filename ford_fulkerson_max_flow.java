import java.util.*;

class MaxFlow {

    private MaxFlow() {}

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

    private static int[][] copyGraph(int[][] graph) {
        return Arrays.stream(graph).map(int[]::clone).toArray(int[][]::new);
    }

    private static List<int[]> tracePath(int[] parent, int source, int sink) {
        List<int[]> edges = new ArrayList<>();
        for (int v = sink; v != source; v = parent[v])
            edges.add(new int[]{parent[v], v});
        return edges;
    }

    private static int findBottleneck(int[][] residualGraph, List<int[]> path) {
        int bottleneck = Integer.MAX_VALUE;
        for (int[] edge : path)
            bottleneck = Math.min(bottleneck, residualGraph[edge[0]][edge[1]]);
        return bottleneck;
    }

    private static void augmentPath(int[][] residualGraph, List<int[]> path, int flow) {
        for (int[] edge : path) {
            residualGraph[edge[0]][edge[1]] -= flow;
            residualGraph[edge[1]][edge[0]] += flow;
        }
    }

    private static void validateGraph(int[][] graph, int source, int sink) {
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
    }

    public static int fordFulkerson(int[][] graph, int source, int sink) {
        validateGraph(graph, source, sink);

        int[][] residualGraph = copyGraph(graph);
        int maxFlow = 0;
        int[] parent;

        while ((parent = bfs(residualGraph, source, sink)) != null) {
            List<int[]> path = tracePath(parent, source, sink);
            int pathFlow = findBottleneck(residualGraph, path);
            augmentPath(residualGraph, path, pathFlow);
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

        System.out.println("The maximum possible flow is " + fordFulkerson(graph, 0, 5));
    }
}
