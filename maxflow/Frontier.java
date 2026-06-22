package maxflow;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The order in which a graph search visits discovered-but-unexplored vertices.
 * This single abstraction is the only thing that distinguishes the breadth-first
 * search behind Edmonds-Karp from the depth-first search behind the textbook
 * Ford-Fulkerson method.
 */
interface Frontier {

    /** Records a newly discovered vertex. */
    void add(int vertex);

    /** Removes and returns the next vertex to explore. */
    int removeNext();

    boolean isEmpty();

    /** A first-in-first-out frontier, yielding breadth-first search. */
    static Frontier fifo() {
        return new DequeFrontier(false);
    }

    /** A last-in-first-out frontier, yielding depth-first search. */
    static Frontier lifo() {
        return new DequeFrontier(true);
    }

    /** A frontier backed by an {@link ArrayDeque}, polled from the head either way. */
    final class DequeFrontier implements Frontier {

        private final Deque<Integer> vertices = new ArrayDeque<>();
        private final boolean lastInFirstOut;

        private DequeFrontier(boolean lastInFirstOut) {
            this.lastInFirstOut = lastInFirstOut;
        }

        @Override
        public void add(int vertex) {
            if (lastInFirstOut) {
                vertices.addFirst(vertex);
            } else {
                vertices.addLast(vertex);
            }
        }

        @Override
        public int removeNext() {
            return vertices.removeFirst();
        }

        @Override
        public boolean isEmpty() {
            return vertices.isEmpty();
        }
    }
}
