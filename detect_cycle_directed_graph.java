import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Detects cycles in a {@link DirectedGraph} via depth-first search with
 * three-color vertex marking. A "back edge" — an edge into a vertex that is
 * still on the active DFS path — proves a cycle, and the active path itself
 * yields the offending vertices.
 *
 * <p>Runs in O(V + E) time and O(V) extra space. Instances are stateless and
 * may be reused across graphs.
 */
final class CycleDetector {

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
     * @return a {@link Cycle} (e.g. {@code 0 -> 1 -> 2 -> 0}), or
     *         {@link Optional#empty()} if the graph is acyclic
     */
    Optional<Cycle> findCycle(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        Mark[] marks = new Mark[vertexCount];
        Arrays.fill(marks, Mark.UNVISITED);
        Deque<Integer> path = new ArrayDeque<>();

        for (int start = 0; start < vertexCount; start++) {
            if (marks[start] == Mark.UNVISITED) {
                List<Integer> cycle = explore(graph, start, marks, path);
                if (cycle != null) {
                    return Optional.of(new Cycle(cycle));
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
