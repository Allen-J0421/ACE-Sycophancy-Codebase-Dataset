package maxflow.path;

/**
 * Finds an augmenting path using depth-first search. Combining this strategy with
 * {@link FordFulkersonSolver} gives the classic Ford-Fulkerson method. It still
 * computes a correct maximum flow for integer capacities, but, unlike
 * {@link BreadthFirstPathFinder}, the number of iterations can depend on the
 * magnitude of the capacities.
 */
public final class DepthFirstPathFinder extends GraphSearchPathFinder {

    @Override
    protected Frontier newFrontier() {
        return Frontier.lifo();
    }

    @Override
    public String name() {
        return "depth-first (Ford-Fulkerson)";
    }
}
