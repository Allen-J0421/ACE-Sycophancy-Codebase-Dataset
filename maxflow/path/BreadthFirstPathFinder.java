package maxflow.path;

/**
 * Finds an augmenting path using breadth-first search, so the path with the
 * fewest edges is always chosen. Combining this strategy with
 * {@link FordFulkersonSolver} gives the Edmonds-Karp algorithm, whose running
 * time is independent of the edge capacities.
 */
public final class BreadthFirstPathFinder extends GraphSearchPathFinder {

    @Override
    protected Frontier newFrontier() {
        return Frontier.fifo();
    }

    @Override
    public String name() {
        return "breadth-first (Edmonds-Karp)";
    }
}
