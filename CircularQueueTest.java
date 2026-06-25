public final class CircularQueueTest {
    private CircularQueueTest() {
    }

    public static void main(String[] args) {
        testWrapAround();
        testFailFastIterator();
        testEmptyAndFullBehavior();
        testNullOfferRejected();
        testLegacyWrapperBehavior();
        System.out.println("CircularQueue checks passed.");
    }

    private static void testWrapAround() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals("[1,2,3]", queue.toString(), "initial order");

        assertEquals(Integer.valueOf(1), queue.dequeue(), "dequeue head");
        queue.enqueue(4);
        assertEquals("[2,3,4]", queue.toString(), "wrap-around order");
        assertEquals(Integer.valueOf(2), queue.peekFront(), "front after wrap-around");
        assertEquals(Integer.valueOf(4), queue.peekRear(), "rear after wrap-around");
    }

    private static void testFailFastIterator() {
        CircularQueue<Integer> queue = new CircularQueue<>(2);
        queue.enqueue(10);
        queue.enqueue(20);

        java.util.Iterator<Integer> iterator = queue.iterator();
        assertTrue(iterator.hasNext(), "iterator should have first item");
        assertEquals(Integer.valueOf(10), iterator.next(), "iterator first item");

        queue.dequeue();
        try {
            iterator.hasNext();
            throw new AssertionError("iterator should have failed fast");
        } catch (java.util.ConcurrentModificationException expected) {
            // expected
        }

        queue = new CircularQueue<>(1);
        queue.enqueue(7);
        java.util.Iterator<Integer> singleItemIterator = queue.iterator();
        assertEquals(Integer.valueOf(7), singleItemIterator.next(), "single item iterator");
        try {
            singleItemIterator.next();
            throw new AssertionError("exhausted iterator should throw");
        } catch (java.util.NoSuchElementException expected) {
            // expected
        }
    }

    private static void testEmptyAndFullBehavior() {
        CircularQueue<Integer> queue = new CircularQueue<>(1);
        assertTrue(queue.isEmpty(), "queue should start empty");
        assertEquals(null, queue.poll(), "poll on empty queue");

        queue.enqueue(99);
        assertTrue(queue.isFull(), "queue should be full");
        assertEquals(0, queue.remainingCapacity(), "remaining capacity");

        try {
            queue.enqueue(100);
            throw new AssertionError("enqueue on full queue should throw");
        } catch (IllegalStateException expected) {
            // expected
        }

        queue.clear();
        assertTrue(queue.isEmpty(), "queue should be empty after clear");
    }

    private static void testNullOfferRejected() {
        CircularQueue<Integer> queue = new CircularQueue<>(2);
        try {
            queue.offer(null);
            throw new AssertionError("null offer should throw");
        } catch (NullPointerException expected) {
            // expected
        }
    }

    private static void testLegacyWrapperBehavior() {
        myQueue queue = new myQueue(2);
        assertEquals(Integer.valueOf(-1), queue.dequeue(), "legacy empty dequeue");
        assertEquals(Integer.valueOf(-1), queue.getFront(), "legacy empty getFront");
        assertEquals(Integer.valueOf(-1), queue.getRear(), "legacy empty getRear");
        queue.enqueue(5);
        queue.enqueue(6);
        queue.enqueue(7);
        assertTrue(queue.isFull(), "legacy queue should stay full");
        assertEquals(Integer.valueOf(5), queue.peekFront(), "legacy front");
        assertEquals(Integer.valueOf(6), queue.peekRear(), "legacy rear");
        assertEquals(Integer.valueOf(5), queue.dequeue(), "legacy dequeue first");
        assertEquals(Integer.valueOf(6), queue.getFront(), "legacy getFront");
        assertEquals(Integer.valueOf(6), queue.getRear(), "legacy getRear");
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }
}
