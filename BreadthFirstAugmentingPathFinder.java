import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public final class BreadthFirstAugmentingPathFinder implements AugmentingPathFinder {
    @Override
    public Optional<AugmentingPath> find(ResidualNetwork network, FlowProblem problem) {
        boolean[] visited = new boolean[network.size()];
        Vertex[] parents = new Vertex[network.size()];
        Vertex[] vertices = network.vertices();
        Queue<Vertex> queue = new ArrayDeque<>();

        queue.add(problem.source());
        visited[problem.source().index()] = true;

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();

            for (Vertex next : vertices) {
                if (visited[next.index()] || !network.hasCapacity(current, next)) {
                    continue;
                }

                parents[next.index()] = current;
                if (next.equals(problem.sink())) {
                    return Optional.of(createPath(network, problem, parents));
                }

                visited[next.index()] = true;
                queue.add(next);
            }
        }

        return Optional.empty();
    }

    private AugmentingPath createPath(ResidualNetwork network, FlowProblem problem, Vertex[] parents) {
        int bottleneck = Integer.MAX_VALUE;

        for (Vertex vertex = problem.sink(); !vertex.equals(problem.source()); vertex = parents[vertex.index()]) {
            Vertex previous = parents[vertex.index()];
            bottleneck = Math.min(bottleneck, network.capacity(previous, vertex));
        }

        return new AugmentingPath(problem.source(), problem.sink(), bottleneck, parents);
    }
}
