import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public final class MaxFlowSolver {
    public MaxFlowResult solve(FlowNetwork network, int source, int sink) {
        validateVertices(network, source, sink);

        int[][] residualGraph = network.copyCapacities();
        int[] parent = new int[network.size()];
        int maxFlow = 0;

        while (findAugmentingPath(residualGraph, source, sink, parent)) {
            int pathFlow = calculatePathFlow(residualGraph, source, sink, parent);
            updateResidualGraph(residualGraph, source, sink, parent, pathFlow);
            maxFlow += pathFlow;
        }

        return new MaxFlowResult(maxFlow, residualGraph);
    }

    public int calculateMaxFlow(int[][] graph, int source, int sink) {
        return solve(new FlowNetwork(graph), source, sink).getMaxFlow();
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

    private void validateVertices(FlowNetwork network, int source, int sink) {
        if (source < 0 || source >= network.size() || sink < 0 || sink >= network.size()) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indices.");
        }
    }
}
