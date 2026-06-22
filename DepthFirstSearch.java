import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * An iterative depth-first traversal of a {@link Graph} that drives a
 * {@link DfsVisitor}. It visits every vertex once, starting a fresh DFS tree at
 * each undiscovered vertex (in ascending order), and reports discovery, tree
 * edges, non-tree edges, and post-order completion through the visitor's hooks.
 *
 * <p>The traversal is direction-agnostic: it follows {@link Graph#edgesFrom(int)}
 * to {@link Edge#other(int)} and reports <em>every</em> edge it considers. On an
 * undirected graph this includes the reverse traversal of the tree edge (the same
 * {@link Edge#id() edge id}) as a non-tree edge; a visitor that must ignore it —
 * such as bridge detection — compares against the tree edge it recorded, while a
 * visitor that treats undirected edges as bidirectional — such as
 * strongly-connected components — processes it. The engine stays free of either
 * interpretation.
 *
 * <p>The traversal is iterative, so it handles arbitrarily deep graphs without
 * risking a {@link StackOverflowError}. Instances are stateless and reusable.
 */
public final class DepthFirstSearch {

    /**
     * Traverses {@code graph}, invoking {@code visitor}'s hooks along the way.
     *
     * @param graph   the graph to traverse
     * @param visitor the visitor to drive
     */
    public void traverse(Graph graph, DfsVisitor visitor) {
        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        for (int root = 0; root < vertexCount; root++) {
            if (!visited[root]) {
                exploreFrom(root, graph, visitor, visited);
            }
        }
    }

    private void exploreFrom(int root, Graph graph, DfsVisitor visitor, boolean[] visited) {
        Deque<Frame> stack = new ArrayDeque<>();
        visited[root] = true;
        visitor.discoverVertex(root);
        stack.push(new Frame(root));

        while (!stack.isEmpty()) {
            Frame frame = stack.peek();
            int v = frame.vertex;
            List<Edge> edges = graph.edgesFrom(v);

            if (frame.nextEdge < edges.size()) {
                Edge edge = edges.get(frame.nextEdge++);
                int w = edge.other(v);
                if (!visited[w]) {
                    visited[w] = true;
                    visitor.treeEdge(v, w, edge);
                    visitor.discoverVertex(w);
                    stack.push(new Frame(w));
                } else {
                    visitor.nonTreeEdge(v, w, edge);
                }
            } else {
                stack.pop();
                visitor.finishVertex(v);
            }
        }
    }

    /** One vertex's state on the explicit DFS stack. */
    private static final class Frame {

        final int vertex;

        /** Index of the next edge to consider from {@link #vertex}. */
        int nextEdge;

        Frame(int vertex) {
            this.vertex = vertex;
        }
    }
}
