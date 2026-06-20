import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

final class ResidualNetwork {
    private static final int NO_PARENT = -1;

    private final int[][] capacities;

    private ResidualNetwork(int[][] capacities) {
        this.capacities = capacities;
    }

    static ResidualNetwork from(int[][] graph) {
        int[][] capacities = new int[graph.length][];

        for (int row = 0; row < graph.length; row++) {
            capacities[row] = Arrays.copyOf(graph[row], graph[row].length);
        }

        return new ResidualNetwork(capacities);
    }

    boolean findAugmentingPath(int source, int sink, int[] parent) {
        boolean[] visited = new boolean[capacities.length];
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(parent, NO_PARENT);
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < capacities.length; v++) {
                if (visited[v] || capacities[u][v] <= 0) {
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

    int bottleneckCapacity(int source, int sink, int[] parent) {
        int pathFlow = Integer.MAX_VALUE;

        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            pathFlow = Math.min(pathFlow, capacities[u][v]);
        }

        return pathFlow;
    }

    void augmentPath(int source, int sink, int[] parent, int pathFlow) {
        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            capacities[u][v] -= pathFlow;
            capacities[v][u] += pathFlow;
        }
    }
}
