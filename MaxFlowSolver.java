import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public final class MaxFlowSolver {
    public int calculateMaxFlow(int[][] graph, int source, int sink) {
        validateGraph(graph, source, sink);

        int[][] residualGraph = copyGraph(graph);
        int[] parent = new int[graph.length];
        int maxFlow = 0;

        while (findAugmentingPath(residualGraph, source, sink, parent)) {
            int pathFlow = calculatePathFlow(residualGraph, source, sink, parent);
            updateResidualGraph(residualGraph, source, sink, parent, pathFlow);
            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean findAugmentingPath(int[][] residualGraph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[residualGraph.length];
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(parent, -1);
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int next = 0; next < residualGraph.length; next++) {
                if (visited[next] || residualGraph[current][next] <= 0) {
                    continue;
                }

                parent[next] = current;
                if (next == sink) {
                    return true;
                }

                visited[next] = true;
                queue.add(next);
            }
        }

        return false;
    }

    private int calculatePathFlow(int[][] residualGraph, int source, int sink, int[] parent) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int previous = parent[vertex];
            pathFlow = Math.min(pathFlow, residualGraph[previous][vertex]);
        }

        return pathFlow;
    }

    private void updateResidualGraph(int[][] residualGraph, int source, int sink, int[] parent, int pathFlow) {
        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int previous = parent[vertex];
            residualGraph[previous][vertex] -= pathFlow;
            residualGraph[vertex][previous] += pathFlow;
        }
    }

    private int[][] copyGraph(int[][] graph) {
        int[][] residualGraph = new int[graph.length][graph.length];

        for (int row = 0; row < graph.length; row++) {
            if (graph[row].length != graph.length) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
            System.arraycopy(graph[row], 0, residualGraph[row], 0, graph.length);
        }

        return residualGraph;
    }

    private void validateGraph(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must not be empty.");
        }
        if (source < 0 || source >= graph.length || sink < 0 || sink >= graph.length) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indices.");
        }
    }
}
