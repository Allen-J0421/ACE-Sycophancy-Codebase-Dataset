import java.util.*;

class MaxFlow {

    private boolean bfs(int[][] residualGraph, int source, int sink, int[] parent) {
        int n = residualGraph.length;
        boolean[] visited = new boolean[n];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < n; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    if (v == sink) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        return false;
    }

    public int fordFulkerson(int[][] graph, int source, int sink) {
        int n = graph.length;
        int[][] residualGraph = new int[n][n];

        for (int u = 0; u < n; u++)
            for (int v = 0; v < n; v++)
                residualGraph[u][v] = graph[u][v];

        int[] parent = new int[n];
        int maxFlow = 0;

        while (bfs(residualGraph, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

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
