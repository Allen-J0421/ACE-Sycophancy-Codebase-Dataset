import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Cycle detection via depth-first search with three-color vertex marking. A
 * "back edge" — an edge into a vertex still on the active DFS path — proves a
 * cycle, and the active path itself yields the offending vertices.
 *
 * <p>The traversal is <em>iterative</em>, driven by an explicit stack rather
 * than recursion, so it handles arbitrarily deep graphs without risking a
 * {@link StackOverflowError}. Runs in O(V + E) time and O(V) extra space.
 *
 * <p>Reconstructing the cycle path comes for free from the traversal, so this
 * implementation uses the default {@link CycleDetector#hasCycle}.
 */
final class DfsCycleDetector implements CycleDetector {

    private enum Mark {
        /** Not yet reached. */
        UNVISITED,
        /** On the current DFS path (its descendants are still being explored). */
        ON_PATH,
        /** Fully explored; cannot be part of a cycle reached from here. */
        DONE
    }

    /** One entry of the explicit DFS stack: a vertex and how far its out-edges have been scanned. */
    private static final class Frame {
        final int vertex;
        final List<Integer> neighbors;
        int next;

        Frame(int vertex, List<Integer> neighbors) {
            this.vertex = vertex;
            this.neighbors = neighbors;
        }
    }

    @Override
    public String name() {
        return "DFS";
    }

    @Override
    public Optional<Cycle> findCycle(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        Mark[] marks = new Mark[vertexCount];
        Arrays.fill(marks, Mark.UNVISITED);

        for (int start = 0; start < vertexCount; start++) {
            if (marks[start] == Mark.UNVISITED) {
                List<Integer> cycle = explore(graph, start, marks);
                if (cycle != null) {
                    return Optional.of(new Cycle(cycle));
                }
            }
        }
        return Optional.empty();
    }

    /** Iterative depth-first walk rooted at {@code root}; returns a cycle if a back edge is found, else {@code null}. */
    private List<Integer> explore(DirectedGraph graph, int root, Mark[] marks) {
        Deque<Frame> stack = new ArrayDeque<>();
        marks[root] = Mark.ON_PATH;
        stack.addLast(new Frame(root, graph.neighbors(root)));

        while (!stack.isEmpty()) {
            Frame frame = stack.peekLast();
            if (frame.next < frame.neighbors.size()) {
                int v = frame.neighbors.get(frame.next++);
                if (marks[v] == Mark.ON_PATH) {
                    return cycleFrom(stack, v);
                }
                if (marks[v] == Mark.UNVISITED) {
                    marks[v] = Mark.ON_PATH;
                    stack.addLast(new Frame(v, graph.neighbors(v)));
                }
            } else { // u's out-edges are exhausted: this is where recursion would return
                marks[frame.vertex] = Mark.DONE;
                stack.removeLast();
            }
        }
        return null;
    }

    /** Slices the active path from its first visit to {@code start}, closing the loop back to {@code start}. */
    private List<Integer> cycleFrom(Deque<Frame> stack, int start) {
        List<Integer> cycle = new ArrayList<>();
        boolean collecting = false;
        for (Frame frame : stack) { // ArrayDeque iterates head -> tail, i.e. root -> current
            if (frame.vertex == start) {
                collecting = true;
            }
            if (collecting) {
                cycle.add(frame.vertex);
            }
        }
        cycle.add(start);
        return cycle;
    }
}
