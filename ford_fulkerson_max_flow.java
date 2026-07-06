import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

interface FlowNetwork {
    List<Integer> neighborsWithCapacity(int u);
    int capacity(int u, int v);
    void updateFlow(int u, int v, int flow);
    int size();
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

    @Override
    public int size() { return size; }

    @Override
    public int capacity(int u, int v) { return capacity[u][v]; }

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

class Edge {
    final int from;
    final int to;

    Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }
}

class AugmentingPath {
    private final List<Edge> edges;

    AugmentingPath(int[] parent, int source, int sink) {
        LinkedList<Edge> path = new LinkedList<>();
        for (int v = sink; v != source; v = parent[v])
            path.addFirst(new Edge(parent[v], v));
        this.edges = Collections.unmodifiableList(path);
    }

    List<Edge> edges() { return edges; }

    int bottleneck(FlowNetwork residual) {
        int flow = Integer.MAX_VALUE;
        for (Edge e : edges)
            flow = Math.min(flow, residual.capacity(e.from, e.to));
        return flow;
    }

    int applyTo(FlowNetwork residual) {
        int flow = bottleneck(residual);
        for (Edge e : edges)
            residual.updateFlow(e.from, e.to, flow);
        return flow;
    }
}

interface PathFinder {
    AugmentingPath find(FlowNetwork residual, int s, int t);
}

class BfsPathFinder implements PathFinder {
    @Override
    public AugmentingPath find(FlowNetwork residual, int s, int t) {
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
}

class DfsPathFinder implements PathFinder {
    @Override
    public AugmentingPath find(FlowNetwork residual, int s, int t) {
        boolean[] visited = new boolean[residual.size()];
        int[] parent = new int[residual.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(s);
        visited[s] = true;
        parent[s] = -1;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (int v : residual.neighborsWithCapacity(u)) {
                if (!visited[v]) {
                    parent[v] = u;
                    if (v == t)
                        return new AugmentingPath(parent, s, t);
                    stack.push(v);
                    visited[v] = true;
                }
            }
        }
        return null;
    }
}

interface ResidualGraphFactory {
    FlowNetwork createResidual(FlowNetwork source);
}

class CopyingResidualGraphFactory implements ResidualGraphFactory {
    @Override
    public FlowNetwork createResidual(FlowNetwork source) {
        int n = source.size();
        int[][] cap = new int[n][n];
        for (int u = 0; u < n; u++)
            for (int v = 0; v < n; v++)
                cap[u][v] = source.capacity(u, v);
        return new FlowGraph(cap);
    }
}

interface FlowOptimizer {
    int augment(AugmentingPath path, FlowNetwork residual);
}

class BottleneckFlowOptimizer implements FlowOptimizer {
    @Override
    public int augment(AugmentingPath path, FlowNetwork residual) {
        return path.applyTo(residual);
    }
}

class MaxFlowResult {
    private final int flow;
    private final FlowNetwork residual;

    MaxFlowResult(int flow, FlowNetwork residual) {
        this.flow = flow;
        this.residual = residual;
    }

    int flow() { return flow; }
    FlowNetwork residual() { return residual; }
}

interface MaxFlowAlgorithm {
    MaxFlowResult compute(FlowNetwork graph, int s, int t);
}

abstract class AugmentingPathAlgorithm implements MaxFlowAlgorithm {
    private final PathFinder pathFinder;
    private final ResidualGraphFactory residualFactory;
    private final FlowOptimizer flowOptimizer;

    AugmentingPathAlgorithm(PathFinder pathFinder, ResidualGraphFactory residualFactory,
                            FlowOptimizer flowOptimizer) {
        this.pathFinder = pathFinder;
        this.residualFactory = residualFactory;
        this.flowOptimizer = flowOptimizer;
    }

    @Override
    public final MaxFlowResult compute(FlowNetwork graph, int s, int t) {
        FlowNetwork residual = residualFactory.createResidual(graph);
        int maxFlow = 0;
        AugmentingPath path;
        while ((path = pathFinder.find(residual, s, t)) != null)
            maxFlow += flowOptimizer.augment(path, residual);
        return new MaxFlowResult(maxFlow, residual);
    }
}

class FordFulkerson extends AugmentingPathAlgorithm {
    FordFulkerson(PathFinder pathFinder, ResidualGraphFactory residualFactory,
                  FlowOptimizer flowOptimizer) {
        super(pathFinder, residualFactory, flowOptimizer);
    }
}

class MaxFlow {
    public static void main(String[] args) throws java.lang.Exception {
        FlowNetwork graph = new FlowGraph(new int[][] {
            { 0, 16, 13, 0, 0, 0 }, { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },  { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },   { 0, 0, 0, 0, 0, 0 }
        });
        MaxFlowAlgorithm algorithm = new FordFulkerson(
            new BfsPathFinder(),
            new CopyingResidualGraphFactory(),
            new BottleneckFlowOptimizer()
        );
        MaxFlowResult result = algorithm.compute(graph, 0, 5);
        System.out.println("The maximum possible flow is " + result.flow());
    }
}
