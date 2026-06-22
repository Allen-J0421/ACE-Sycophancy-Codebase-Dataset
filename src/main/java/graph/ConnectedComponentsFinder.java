package graph;

import graph.traversal.BreadthFirstTraversal;
import graph.traversal.TraversalStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Finds the {@link Components} of an undirected {@link Graph}.
 *
 * <p>The graph traversal is pluggable via a {@link TraversalStrategy}: the
 * finder repeatedly traverses from each not-yet-visited vertex to collect one
 * component at a time. Breadth-first and depth-first traversal yield the same
 * partition, differing only in the order of vertices within each component.
 */
public final class ConnectedComponentsFinder {

    private final TraversalStrategy traversal;

    /** Creates a finder that traverses breadth-first. */
    public ConnectedComponentsFinder() {
        this(new BreadthFirstTraversal());
    }

    /**
     * Creates a finder that uses the given traversal strategy.
     *
     * @throws NullPointerException if {@code traversal} is {@code null}
     */
    public ConnectedComponentsFinder(TraversalStrategy traversal) {
        this.traversal = Objects.requireNonNull(traversal, "traversal");
    }

    /** Returns the connected components of {@code graph}. */
    public Components find(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(traversal.traverseFrom(graph, vertex, visited));
            }
        }
        return new Components(components, graph.vertexCount());
    }
}
