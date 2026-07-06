import collections.CircularQueue;
import collections.Queue;

class QueueTest {

    private static void assertEqual(Object expected, Object actual, String label) {
        if (!expected.equals(actual))
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition)
            throw new AssertionError(label + ": expected true");
    }

    private static void testEnqueueUpdatesEnds() {
        Queue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        assertEqual(10, q.getFront(), "front after 3 enqueues");
        assertEqual(30, q.getRear(), "rear after 3 enqueues");
        assertEqual(3, q.size(), "size after 3 enqueues");
    }

    private static void testDequeueAdvancesFront() {
        Queue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.dequeue();
        assertEqual(20, q.getFront(), "front after dequeue");
        assertEqual(30, q.getRear(), "rear after dequeue");
        assertEqual(2, q.size(), "size after dequeue");
    }

    private static void testWrapAround() {
        Queue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.dequeue();
        q.enqueue(40);
        assertEqual(20, q.getFront(), "front after wrap-around enqueue");
        assertEqual(40, q.getRear(), "rear after wrap-around enqueue");
    }

    private static void testEmptyQueueExceptions() {
        Queue<Integer> q = new CircularQueue<>(2);
        assertTrue(q.isEmpty(), "isEmpty on new queue");
        q.enqueue(1);
        q.dequeue();
        assertTrue(q.isEmpty(), "isEmpty after draining queue");
        try {
            q.dequeue();
            throw new AssertionError("dequeue on empty queue: expected IllegalStateException");
        } catch (IllegalStateException e) { /* expected */ }
    }

    private static void testDynamicResizing() {
        Queue<Integer> q = new CircularQueue<>(2);
        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }
        assertEqual(10, q.size(), "size after growing past initial capacity");
        assertEqual(0, q.getFront(), "front preserved after resize");
        assertEqual(9, q.getRear(), "rear preserved after resize");
        for (int i = 0; i < 10; i++) {
            assertEqual(i, q.dequeue(), "dequeue order after resize");
        }
        assertTrue(q.isEmpty(), "isEmpty after draining resized queue");
    }

    private static void testStringQueue() {
        Queue<String> q = new CircularQueue<>(3);
        q.enqueue("alpha");
        q.enqueue("beta");
        q.enqueue("gamma");
        assertEqual("alpha", q.getFront(), "front of string queue");
        assertEqual("gamma", q.getRear(), "rear of string queue");
        assertEqual("alpha", q.dequeue(), "dequeue returns front value");
        assertEqual("beta", q.getFront(), "front after dequeue");
    }

    public static void main(String[] args) {
        testEnqueueUpdatesEnds();
        testDequeueAdvancesFront();
        testWrapAround();
        testEmptyQueueExceptions();
        testDynamicResizing();
        testStringQueue();
        System.out.println("All tests passed.");
    }
}
