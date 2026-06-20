import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public final class MaxFlow {
    public int fordFulkerson(int[][] graph, int source, int sink) {
        return maximumFlow(graph, source, sink);
    }

    public int maximumFlow(int[][] graph, int source, int sink) {
        validateGraph(graph, source, sink);

        int[][] residualGraph = copyGraph(graph);
        int[] parent = new int[graph.length];
        int maxFlow = 0;

        while (findAugmentingPath(residualGraph, source, sink, parent)) {
            int pathFlow = findBottleneckCapacity(residualGraph, source, sink, parent);
            updateResidualCapacities(residualGraph, source, sink, parent, pathFlow);
            maxFlow = Math.addExact(maxFlow, pathFlow);
        }

        return maxFlow;
    }

    private static boolean findAugmentingPath(
        int[][] residualGraph,
        int source,
        int sink,
        int[] parent
    ) {
        boolean[] visited = new boolean[residualGraph.length];
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(parent, -1);
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < residualGraph.length; v++) {
                if (visited[v] || residualGraph[u][v] <= 0) {
                    continue;
                }

                parent[v] = u;
                if (v == sink) {
                    return true;
                }

                visited[v] = true;
                queue.add(v);
            }
        }

        return false;
    }

    private static int findBottleneckCapacity(
        int[][] residualGraph,
        int source,
        int sink,
        int[] parent
    ) {
        int pathFlow = Integer.MAX_VALUE;

        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            pathFlow = Math.min(pathFlow, residualGraph[u][v]);
        }

        return pathFlow;
    }

    private static void updateResidualCapacities(
        int[][] residualGraph,
        int source,
        int sink,
        int[] parent,
        int pathFlow
    ) {
        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            residualGraph[u][v] -= pathFlow;
            residualGraph[v][u] += pathFlow;
        }
    }

    private static int[][] copyGraph(int[][] graph) {
        int[][] copy = new int[graph.length][];

        for (int row = 0; row < graph.length; row++) {
            copy[row] = Arrays.copyOf(graph[row], graph[row].length);
        }

        return copy;
    }

    private static void validateGraph(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        if (!isValidVertex(source, graph.length) || !isValidVertex(sink, graph.length)) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indexes.");
        }

        if (source == sink) {
            throw new IllegalArgumentException("Source and sink must be different vertices.");
        }

        for (int row = 0; row < graph.length; row++) {
            if (graph[row] == null || graph[row].length != graph.length) {
                throw new IllegalArgumentException("Graph must be a square capacity matrix.");
            }

            for (int capacity : graph[row]) {
                if (capacity < 0) {
                    throw new IllegalArgumentException("Graph capacities must be non-negative.");
                }
            }
        }
    }

    private static boolean isValidVertex(int vertex, int vertexCount) {
        return vertex >= 0 && vertex < vertexCount;
    }

    public static void main(String[] args) {
        MaxFlowDemo.main(args);
    }
}
