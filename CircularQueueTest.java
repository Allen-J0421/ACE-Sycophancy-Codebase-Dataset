import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

public final class CircularQueueTest {

    private CircularQueueTest() {
    }

    public static void main(String[] args) {
        testBasicQueueOperations();
        testWrapAroundBehavior();
        testCapacityBoundaries();
        testClearAndRemainingCapacity();
        testIteratorTraversalAndFailFastBehavior();
        testNullElementsAreRejected();

        System.out.println("All CircularQueue tests passed.");
    }

    private static void testBasicQueueOperations() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        queue.enqueue(10);
        queue.enqueue(20);

        assertEquals(2, queue.size(), "enqueue should increase the size");
        assertEquals(1, queue.remainingCapacity(), "remaining capacity should decrease after enqueue");
        assertEquals(Integer.valueOf(10), queue.peek(), "peek should return the front element");
        assertEquals(Integer.valueOf(10), queue.peekFront(), "peekFront should return the front element");
        assertEquals(Integer.valueOf(20), queue.peekRear(), "peekRear should return the rear element");
        assertEquals(Integer.valueOf(10), queue.dequeue(), "dequeue should remove from the front");
        assertEquals(Integer.valueOf(20), queue.poll(), "poll should remove and return the next element");
        assertNull(queue.poll(), "poll should return null when the queue is empty");
        assertThrows(IllegalStateException.class, queue::dequeue, "dequeue should throw when the queue is empty");
    }

    private static void testWrapAroundBehavior() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.dequeue();
        queue.enqueue(4);

        assertEquals("[2, 3, 4]", queue.toString(), "wrap-around should preserve iteration order");
        assertEquals(Integer.valueOf(2), queue.peekFront(), "front should advance after dequeue");
        assertEquals(Integer.valueOf(4), queue.peekRear(), "rear should track wrapped insertion");
    }

    private static void testCapacityBoundaries() {
        CircularQueue<Integer> queue = new CircularQueue<>(2);

        assertTrue(queue.offer(7), "offer should succeed when there is room");
        assertTrue(queue.offer(8), "offer should succeed until the queue is full");
        assertFalse(queue.offer(9), "offer should return false when the queue is full");
        assertThrows(IllegalStateException.class, () -> queue.enqueue(9), "enqueue should throw when the queue is full");
        assertEquals(0, queue.remainingCapacity(), "remaining capacity should be zero when full");
    }

    private static void testClearAndRemainingCapacity() {
        CircularQueue<String> queue = new CircularQueue<>(2);

        queue.enqueue("a");
        queue.enqueue("b");
        queue.clear();

        assertTrue(queue.isEmpty(), "clear should empty the queue");
        assertEquals(2, queue.remainingCapacity(), "clear should restore the full remaining capacity");
        assertEquals("[]", queue.toString(), "clear should reset the string representation");
    }

    private static void testIteratorTraversalAndFailFastBehavior() {
        CircularQueue<Integer> queue = new CircularQueue<>(4);

        queue.enqueue(5);
        queue.enqueue(6);
        queue.enqueue(7);

        StringBuilder builder = new StringBuilder();
        for (int value : queue) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(value);
        }
        assertEquals("5,6,7", builder.toString(), "iterator should traverse from head to tail");

        Iterator<Integer> iterator = queue.iterator();
        assertTrue(iterator.hasNext(), "iterator should have elements before mutation");
        queue.dequeue();
        assertThrows(
                ConcurrentModificationException.class,
                iterator::next,
                "iterator should fail fast after queue mutation");
    }

    private static void testNullElementsAreRejected() {
        CircularQueue<String> queue = new CircularQueue<>(1);

        assertThrows(NullPointerException.class, () -> queue.offer(null), "offer should reject null elements");
        assertThrows(NullPointerException.class, () -> queue.enqueue(null), "enqueue should reject null elements");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + ": expected null but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static <T extends Throwable> void assertThrows(
            Class<T> expectedType,
            ThrowingRunnable action,
            String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(
                    message + ": expected " + expectedType.getSimpleName()
                            + " but caught " + throwable.getClass().getSimpleName(),
                    throwable);
        }

        throw new AssertionError(message + ": expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
