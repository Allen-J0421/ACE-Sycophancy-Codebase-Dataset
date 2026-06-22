import java.util.ArrayList;
import java.util.List;

/**
 * Finds the articulation points (cut vertices) of an undirected graph using
 * Tarjan's depth-first search algorithm.
 *
 * <p>A vertex is an articulation point if removing it (and its incident edges)
 * increases the number of connected components of the graph. The algorithm runs
 * in {@code O(V + E)} time.
 *
 * <p>The DFS state is kept in instance fields, so a finder instance is single-use
 * and not thread-safe; call {@link #find(Graph)} on a fresh instance per graph.
 */
public final class ArticulationPointFinder {

    /** Discovery time of each vertex in the DFS traversal. */
    private int[] discovery;

    /** Lowest discovery time reachable from each vertex's DFS subtree. */
    private int[] low;

    private boolean[] visited;
    private boolean[] isArticulationPoint;

    /** Monotonically increasing DFS timestamp counter. */
    private int timer;

    /**
     * Returns the articulation points of the given graph in ascending order.
     *
     * @param graph the graph to analyze
     * @return the sorted list of articulation-point vertices (empty if none)
     */
    public List<Integer> find(Graph graph) {
        int vertexCount = graph.vertexCount();
        discovery = new int[vertexCount];
        low = new int[vertexCount];
        visited = new boolean[vertexCount];
        isArticulationPoint = new boolean[vertexCount];
        timer = 0;

        for (int u = 0; u < vertexCount; u++) {
            if (!visited[u]) {
                dfs(graph, u, -1);
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int u = 0; u < vertexCount; u++) {
            if (isArticulationPoint[u]) {
                result.add(u);
            }
        }
        return result;
    }

    private void dfs(Graph graph, int u, int parent) {
        visited[u] = true;
        discovery[u] = low[u] = ++timer;
        int rootChildren = 0;

        for (int v : graph.neighbors(u)) {
            if (!visited[v]) {
                rootChildren++;
                dfs(graph, v, u);
                low[u] = Math.min(low[u], low[v]);

                // A non-root vertex is a cut vertex when one of its children's
                // subtrees cannot reach above it via a back edge.
                if (parent != -1 && low[v] >= discovery[u]) {
                    isArticulationPoint[u] = true;
                }
            } else if (v != parent) {
                // Back edge: update the low-link, but ignore the edge to the parent.
                low[u] = Math.min(low[u], discovery[v]);
            }
        }

        // The DFS root is a cut vertex only if it has more than one DFS child.
        if (parent == -1 && rootChildren > 1) {
            isArticulationPoint[u] = true;
        }
    }
}
