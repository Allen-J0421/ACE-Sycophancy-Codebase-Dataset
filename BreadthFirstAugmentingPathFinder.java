import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public final class BreadthFirstAugmentingPathFinder implements AugmentingPathFinder {
    @Override
    public Optional<AugmentingPath> find(ResidualNetwork network, FlowProblem problem) {
        VertexTraversalState traversal = new VertexTraversalState(network.size(), problem.source());
        Vertex[] vertices = network.vertices();
        Queue<Vertex> queue = new ArrayDeque<>();

        queue.add(problem.source());

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();

            for (Vertex next : vertices) {
                if (traversal.hasVisited(next) || !network.hasCapacity(current, next)) {
                    continue;
                }

                traversal.visit(next, current);
                if (next.equals(problem.sink())) {
                    return Optional.of(traversal.createPath(network, problem.sink()));
                }

                queue.add(next);
            }
        }

        return Optional.empty();
    }
}
