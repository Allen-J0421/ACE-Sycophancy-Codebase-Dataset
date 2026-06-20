import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;

public final class ResidualNetwork {
    private final int[][] capacities;

    public ResidualNetwork(FlowNetwork network) {
        capacities = network.copyCapacities();
    }

    public Optional<AugmentingPath> findAugmentingPath(int source, int sink) {
        boolean[] visited = new boolean[capacities.length];
        int[] parents = new int[capacities.length];
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(parents, -1);
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int next = 0; next < capacities.length; next++) {
                if (visited[next] || capacities[current][next] <= 0) {
                    continue;
                }

                parents[next] = current;
                if (next == sink) {
                    return Optional.of(new AugmentingPath(source, sink, calculateBottleneck(source, sink, parents), parents));
                }

                visited[next] = true;
                queue.add(next);
            }
        }

        return Optional.empty();
    }

    public void augment(AugmentingPath path) {
        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int previous = path.parentOf(vertex);
            capacities[previous][vertex] -= path.bottleneck();
            capacities[vertex][previous] += path.bottleneck();
        }
    }

    public int[][] snapshot() {
        return IntMatrix.copyOf(capacities);
    }

    private int calculateBottleneck(int source, int sink, int[] parents) {
        int bottleneck = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = parents[vertex]) {
            int previous = parents[vertex];
            bottleneck = Math.min(bottleneck, capacities[previous][vertex]);
        }

        return bottleneck;
    }
}
