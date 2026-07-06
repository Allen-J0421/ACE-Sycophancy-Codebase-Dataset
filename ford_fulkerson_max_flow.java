import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

interface FlowNetwork {
    List<Integer> neighborsWithCapacity(int u);
    int capacity(int u, int v);
    void updateFlow(int u, int v, int flow);
    int size();
    FlowNetwork copy();
}

class FlowGraph implements FlowNetwork {
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

    @Override
    public FlowNetwork copy() {
        int[][] cap = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                cap[i][j] = capacity[i][j];
        return new FlowGraph(size, cap);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity(int u, int v) {
        return capacity[u][v];
    }

    @Override
    public void updateFlow(int u, int v, int flow) {
        capacity[u][v] -= flow;
        capacity[v][u] += flow;
    }

    @Override
    public List<Integer> neighborsWithCapacity(int u) {
        List<Integer> neighbors = new ArrayList<>();
        for (int v = 0; v < size; v++)
            if (capacity[u][v] > 0)
                neighbors.add(v);
        return neighbors;
    }
}

class AugmentingPath {
    private final int[] parent;
    private final int source;
    private final int sink;

    AugmentingPath(int[] parent, int source, int sink) {
        this.parent = parent;
        this.source = source;
        this.sink = sink;
    }

    int bottleneck(FlowNetwork residual) {
        int flow = Integer.MAX_VALUE;
        for (int v = sink; v != source; v = parent[v])
            flow = Math.min(flow, residual.capacity(parent[v], v));
        return flow;
    }

    int applyTo(FlowNetwork residual) {
        int flow = bottleneck(residual);
        for (int v = sink; v != source; v = parent[v])
            residual.updateFlow(parent[v], v, flow);
        return flow;
    }
}

interface MaxFlowAlgorithm {
    int compute(FlowNetwork graph, int s, int t);
}

class FordFulkerson implements MaxFlowAlgorithm {
    private AugmentingPath bfs(FlowNetwork residual, int s, int t) {
        boolean[] visited = new boolean[residual.size()];
        int[] parent = new int[residual.size()];
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : residual.neighborsWithCapacity(u)) {
                if (!visited[v]) {
                    parent[v] = u;
                    if (v == t)
                        return new AugmentingPath(parent, s, t);
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }
        return null;
    }

    @Override
    public int compute(FlowNetwork graph, int s, int t) {
        FlowNetwork residual = graph.copy();
        int maxFlow = 0;
        AugmentingPath path;
        while ((path = bfs(residual, s, t)) != null)
            maxFlow += path.applyTo(residual);
        return maxFlow;
    }
}

class MaxFlow {
    public static void main(String[] args) throws java.lang.Exception {
        FlowNetwork graph = new FlowGraph(new int[][] {
            { 0, 16, 13, 0, 0, 0 }, { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },  { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },   { 0, 0, 0, 0, 0, 0 }
        });
        MaxFlowAlgorithm algorithm = new FordFulkerson();
        System.out.println("The maximum possible flow is "
                           + algorithm.compute(graph, 0, 5));
    }
}
