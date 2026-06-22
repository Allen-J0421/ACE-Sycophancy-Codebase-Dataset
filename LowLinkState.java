import java.util.Arrays;

/**
 * Shared depth-first-search state for low-link algorithms — Tarjan's articulation
 * points / bridges ({@link GraphConnectivity}) and strongly connected components
 * ({@link StronglyConnectedComponents}).
 *
 * <p>It owns each vertex's <em>discovery time</em> (the order in which the DFS
 * first reaches it) and its <em>low-link</em> (the smallest discovery time
 * reachable from its DFS subtree via tree edges plus at most one back/cross edge),
 * and the three operations that maintain them. It deliberately does <em>not</em>
 * own the traversal control flow, the neighbor iteration, or the
 * algorithm-specific verdicts: those differ between the undirected and directed
 * algorithms and remain with each caller.
 *
 * <p>This is a plain mutable value object, not thread-safe; each traversal creates
 * its own instance.
 */
public final class LowLinkState {

    private static final int UNVISITED = -1;

    private final int[] discovery;
    private final int[] low;

    /** Monotonically increasing discovery counter. */
    private int timer;

    /**
     * Creates traversal state for a graph with {@code vertexCount} vertices, all
     * initially undiscovered.
     */
    public LowLinkState(int vertexCount) {
        discovery = new int[vertexCount];
        low = new int[vertexCount];
        Arrays.fill(discovery, UNVISITED);
    }

    /** Returns whether {@code v} has been visited yet. */
    public boolean isDiscovered(int v) {
        return discovery[v] != UNVISITED;
    }

    /**
     * Records the first visit to {@code v}, stamping its discovery time and
     * initializing its low-link to the same value.
     */
    public void discover(int v) {
        discovery[v] = low[v] = timer++;
    }

    /** Returns the discovery time of {@code v}. */
    public int discovery(int v) {
        return discovery[v];
    }

    /** Returns the current low-link value of {@code v}. */
    public int low(int v) {
        return low[v];
    }

    /**
     * Relaxes {@code v}'s low-link against an already-discovered {@code ancestor}
     * reached by a non-tree (back or cross) edge:
     * {@code low[v] = min(low[v], discovery[ancestor])}.
     */
    public void relaxAgainstAncestor(int v, int ancestor) {
        low[v] = Math.min(low[v], discovery[ancestor]);
    }

    /**
     * Folds a fully explored {@code child}'s low-link into its {@code parent}:
     * {@code low[parent] = min(low[parent], low[child])}.
     */
    public void propagateFromChild(int parent, int child) {
        low[parent] = Math.min(low[parent], low[child]);
    }

    /**
     * Returns whether {@code v} is the root of its low-link subtree, i.e. nothing
     * in its subtree reaches above it ({@code low[v] == discovery[v]}). This is the
     * SCC-root condition in Tarjan's strongly-connected-components algorithm.
     */
    public boolean isLowLinkRoot(int v) {
        return low[v] == discovery[v];
    }
}
