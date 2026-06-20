import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;

public final class BreadthFirstAugmentingPathFinder implements AugmentingPathFinder {
    @Override
    public Optional<AugmentingPath> find(ResidualNetwork network, FlowProblem problem) {
        boolean[] visited = new boolean[network.size()];
        int[] parents = new int[network.size()];
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(parents, -1);
        queue.add(problem.source());
        visited[problem.source()] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int next = 0; next < network.size(); next++) {
                if (visited[next] || !network.hasCapacity(current, next)) {
                    continue;
                }

                parents[next] = current;
                if (next == problem.sink()) {
                    return Optional.of(createPath(network, problem, parents));
                }

                visited[next] = true;
                queue.add(next);
            }
        }

        return Optional.empty();
    }

    private AugmentingPath createPath(ResidualNetwork network, FlowProblem problem, int[] parents) {
        int bottleneck = Integer.MAX_VALUE;

        for (int vertex = problem.sink(); vertex != problem.source(); vertex = parents[vertex]) {
            int previous = parents[vertex];
            bottleneck = Math.min(bottleneck, network.capacity(previous, vertex));
        }

        return new AugmentingPath(problem.source(), problem.sink(), bottleneck, parents);
    }
}
