import java.util.LinkedList;

class FlowGraph {
    private final int size;
    private final int[][] capacity;

    FlowGraph(int[][] matrix) {
        this.size = matrix.length;
        this.capacity = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                this.capacity[i][j] = matrix[i][j];
    }

    private FlowGraph(int size, int[][] capacity) {
        this.size = size;
        this.capacity = capacity;
    }

    FlowGraph copy() {
        int[][] cap = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                cap[i][j] = capacity[i][j];
        return new FlowGraph(size, cap);
    }

    int size() {
        return size;
    }

    int capacity(int u, int v) {
        return capacity[u][v];
    }

    void updateFlow(int u, int v, int flow) {
        capacity[u][v] -= flow;
        capacity[v][u] += flow;
    }
}

class MaxFlow {
    private boolean bfs(FlowGraph residual, int s, int t, int[] parent) {
        boolean[] visited = new boolean[residual.size()];
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < residual.size(); v++) {
                if (!visited[v] && residual.capacity(u, v) > 0) {
                    if (v == t) {
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

    int fordFulkerson(FlowGraph graph, int s, int t) {
        FlowGraph residual = graph.copy();
        int[] parent = new int[graph.size()];
        int maxFlow = 0;

        while (bfs(residual, s, t, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual.capacity(u, v));
            }
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                residual.updateFlow(u, v, pathFlow);
            }
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    public static void main(String[] args) throws java.lang.Exception {
        FlowGraph graph = new FlowGraph(new int[][] {
            { 0, 16, 13, 0, 0, 0 }, { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },  { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },   { 0, 0, 0, 0, 0, 0 }
        });
        MaxFlow m = new MaxFlow();
        System.out.println("The maximum possible flow is "
                           + m.fordFulkerson(graph, 0, 5));
    }
}
