package maxflow.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Exercises the package-private {@link Frontier} strategy directly: it is the one
 * abstraction that distinguishes breadth-first from depth-first traversal.
 */
@DisplayName("Frontier")
class FrontierTest {

    @Test
    @DisplayName("fifo() removes vertices in the order they were added (breadth-first)")
    void fifoOrder() {
        Frontier frontier = Frontier.fifo();
        frontier.add(1);
        frontier.add(2);
        frontier.add(3);
        assertEquals(1, frontier.removeNext());
        assertEquals(2, frontier.removeNext());
        assertEquals(3, frontier.removeNext());
        assertTrue(frontier.isEmpty());
    }

    @Test
    @DisplayName("lifo() removes the most recently added vertex first (depth-first)")
    void lifoOrder() {
        Frontier frontier = Frontier.lifo();
        frontier.add(1);
        frontier.add(2);
        frontier.add(3);
        assertEquals(3, frontier.removeNext());
        assertEquals(2, frontier.removeNext());
        assertEquals(1, frontier.removeNext());
        assertTrue(frontier.isEmpty());
    }

    @Test
    @DisplayName("isEmpty tracks the presence of vertices")
    void isEmpty() {
        Frontier frontier = Frontier.fifo();
        assertTrue(frontier.isEmpty());
        frontier.add(7);
        assertFalse(frontier.isEmpty());
        frontier.removeNext();
        assertTrue(frontier.isEmpty());
    }
}
