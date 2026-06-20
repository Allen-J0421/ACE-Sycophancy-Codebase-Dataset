import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

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

        Optional<List<Integer>> cycle = new CycleDetector().findCycle(graph);
        if (cycle.isPresent()) {
            System.out.println("true (cycle: " + formatPath(cycle.get()) + ")");
        } else {
            System.out.println("false");
        }
    }

    /** Renders a cycle such as {@code [0, 1, 2, 0]} as {@code 0 -> 1 -> 2 -> 0}. */
    private static String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(path.get(i));
        }
        return sb.toString();
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
 * Detects cycles in a {@link DirectedGraph} via depth-first search with
 * three-color vertex marking. A "back edge" — an edge into a vertex that is
 * still on the active DFS path — proves a cycle, and the active path itself
 * yields the offending vertices.
 *
 * <p>Runs in O(V + E) time and O(V) extra space.
 */
class CycleDetector {

    private enum Mark {
        /** Not yet reached. */
        UNVISITED,
        /** On the current DFS path (its descendants are still being explored). */
        ON_PATH,
        /** Fully explored; cannot be part of a cycle reached from here. */
        DONE
    }

    /** Returns {@code true} if {@code graph} contains at least one directed cycle. */
    boolean hasCycle(DirectedGraph graph) {
        return findCycle(graph).isPresent();
    }

    /**
     * Finds one cycle if the graph contains any.
     *
     * @return the cycle as a closed vertex list (e.g. {@code [0, 1, 2, 0]}),
     *         or {@link Optional#empty()} if the graph is acyclic
     */
    Optional<List<Integer>> findCycle(DirectedGraph graph) {
        int vertexCount = graph.vertexCount();
        Mark[] marks = new Mark[vertexCount];
        Arrays.fill(marks, Mark.UNVISITED);
        Deque<Integer> path = new ArrayDeque<>();

        for (int start = 0; start < vertexCount; start++) {
            if (marks[start] == Mark.UNVISITED) {
                List<Integer> cycle = explore(graph, start, marks, path);
                if (cycle != null) {
                    return Optional.of(cycle);
                }
            }
        }
        return Optional.empty();
    }

    /** Depth-first walk from {@code u}; returns a cycle if a back edge is found, else {@code null}. */
    private List<Integer> explore(DirectedGraph graph, int u, Mark[] marks, Deque<Integer> path) {
        marks[u] = Mark.ON_PATH;
        path.addLast(u);

        for (int v : graph.neighbors(u)) {
            if (marks[v] == Mark.ON_PATH) {
                return cycleFrom(path, v);
            }
            if (marks[v] == Mark.UNVISITED) {
                List<Integer> cycle = explore(graph, v, marks, path);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        path.removeLast();
        marks[u] = Mark.DONE;
        return null;
    }

    /** Slices the current path from its first visit to {@code start}, closing the loop back to {@code start}. */
    private List<Integer> cycleFrom(Deque<Integer> path, int start) {
        List<Integer> cycle = new ArrayList<>();
        boolean collecting = false;
        for (int vertex : path) { // ArrayDeque iterates head -> tail, i.e. root -> current
            if (vertex == start) {
                collecting = true;
            }
            if (collecting) {
                cycle.add(vertex);
            }
        }
        cycle.add(start);
        return cycle;
    }
}
