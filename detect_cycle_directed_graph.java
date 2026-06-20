import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Demonstrates cycle detection on a directed graph.
 *
 * <p>The work is split across three responsibilities:
 * <ul>
 *   <li>{@link DirectedGraph} owns the graph representation and its invariants.</li>
 *   <li>{@link CycleDetector} owns the algorithm, decoupled from any concrete storage.</li>
 *   <li>This class wires them together for a small demonstration.</li>
 * </ul>
 */
public class DetectCycle {

    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);

        boolean hasCycle = new CycleDetector().hasCycle(graph);
        System.out.println(hasCycle);
    }
}

/**
 * A directed graph over a fixed set of vertices {@code 0..vertexCount-1},
 * backed by adjacency lists. Parallel edges are permitted; self-loops are
 * allowed and (correctly) report as cycles.
 */
class DirectedGraph {

    private final List<List<Integer>> adjacency;

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    DirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /** Returns the number of vertices in the graph. */
    int vertexCount() {
        return adjacency.size();
    }

    /**
     * Adds a directed edge {@code from -> to}.
     *
     * @throws IndexOutOfBoundsException if either endpoint is not a valid vertex
     */
    void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacency.get(from).add(to);
    }

    /** Returns an unmodifiable view of the out-neighbors of {@code vertex}. */
    List<Integer> neighbors(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException(
                    "vertex " + vertex + " out of range [0, " + adjacency.size() + ")");
        }
    }
}

/**
 * Detects whether a {@link DirectedGraph} contains a cycle using Kahn's
 * algorithm: a directed graph is acyclic if and only if a topological
 * ordering covering every vertex exists. If fewer vertices can be ordered
 * than the graph contains, the remainder lie on at least one cycle.
 *
 * <p>Runs in O(V + E) time and O(V) extra space.
 */
class CycleDetector {

    /** Returns {@code true} if {@code graph} contains at least one directed cycle. */
    boolean hasCycle(DirectedGraph graph) {
        int vertexCount = graph.vertexCount();
        int[] inDegree = new int[vertexCount];

        for (int u = 0; u < vertexCount; u++) {
            for (int v : graph.neighbors(u)) {
                inDegree[v]++;
            }
        }

        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < vertexCount; v++) {
            if (inDegree[v] == 0) {
                ready.add(v);
            }
        }

        int ordered = 0;
        while (!ready.isEmpty()) {
            int u = ready.poll();
            ordered++;
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    ready.add(v);
                }
            }
        }

        return ordered != vertexCount;
    }
}
