interface IntQueue {
    void enqueue(int value);
    int dequeue();
    int getFront();
    int getRear();
    int size();
    boolean isEmpty();
}

class CircularQueue implements IntQueue {

    private int[] arr;

    private int front;

    private int size;

    private int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        arr = new int[capacity];
        front = 0;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int rearIndex() {
        return (front + size - 1) % capacity;
    }

    private void requireNonFull() {
        if (size == capacity) throw new IllegalStateException("Queue is full");
    }

    private void requireNonEmpty() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
    }

    public void enqueue(int value) {
        requireNonFull();
        arr[(rearIndex() + 1) % capacity] = value;
        size++;
    }

    public int dequeue() {
        requireNonEmpty();
        int value = arr[front];
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    public int getFront() {
        requireNonEmpty();
        return arr[front];
    }

    public int getRear() {
        requireNonEmpty();
        return arr[rearIndex()];
    }

    private static void assertEqual(int expected, int actual, String label) {
        if (expected != actual)
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition)
            throw new AssertionError(label + ": expected true");
    }

    private static void testEnqueueUpdatesEnds() {
        IntQueue q = new CircularQueue(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        assertEqual(10, q.getFront(), "front after 3 enqueues");
        assertEqual(30, q.getRear(), "rear after 3 enqueues");
        assertEqual(3, q.size(), "size after 3 enqueues");
    }

    private static void testDequeueAdvancesFront() {
        IntQueue q = new CircularQueue(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.dequeue();
        assertEqual(20, q.getFront(), "front after dequeue");
        assertEqual(30, q.getRear(), "rear after dequeue");
        assertEqual(2, q.size(), "size after dequeue");
    }

    private static void testWrapAround() {
        IntQueue q = new CircularQueue(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.dequeue();
        q.enqueue(40);
        assertEqual(20, q.getFront(), "front after wrap-around enqueue");
        assertEqual(40, q.getRear(), "rear after wrap-around enqueue");
    }

    private static void testIsEmptyAndFullExceptions() {
        IntQueue q = new CircularQueue(2);
        assertTrue(q.isEmpty(), "isEmpty on new queue");
        q.enqueue(1);
        q.enqueue(2);
        try {
            q.enqueue(3);
            throw new AssertionError("enqueue on full queue: expected IllegalStateException");
        } catch (IllegalStateException e) { /* expected */ }
        q.dequeue();
        q.dequeue();
        try {
            q.dequeue();
            throw new AssertionError("dequeue on empty queue: expected IllegalStateException");
        } catch (IllegalStateException e) { /* expected */ }
        assertTrue(q.isEmpty(), "isEmpty after draining queue");
    }

    public static void main(String[] args) {
        testEnqueueUpdatesEnds();
        testDequeueAdvancesFront();
        testWrapAround();
        testIsEmptyAndFullExceptions();
        System.out.println("All tests passed.");
    }
}
