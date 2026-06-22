class myQueue {

    private static final int EMPTY_VALUE = -1;
    private static final String FULL_MESSAGE = "Queue is full!";
    private static final String EMPTY_MESSAGE = "Queue is empty!";

    private final int[] elements;
    private int front;
    private int size;

    public myQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive");
        }

        elements = new int[capacity];
        front = 0;
        size = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            System.out.println(FULL_MESSAGE);
            return;
        }

        elements[nextRearIndex()] = value;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println(EMPTY_MESSAGE);
            return EMPTY_VALUE;
        }

        int value = elements[front];
        front = (front + 1) % capacity();
        size--;
        return value;
    }

    public int getFront() {
        if (isEmpty()) {
            return EMPTY_VALUE;
        }

        return elements[front];
    }

    public int getRear() {
        if (isEmpty()) {
            return EMPTY_VALUE;
        }

        return elements[rearIndex()];
    }

    private boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == capacity();
    }

    private int capacity() {
        return elements.length;
    }

    private int nextRearIndex() {
        return (front + size) % capacity();
    }

    private int rearIndex() {
        return (front + size - 1) % capacity();
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        myQueue queue = new myQueue(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        printEnds(queue);
        queue.dequeue();
        printEnds(queue);
        queue.enqueue(40);
        printEnds(queue);
    }

    private static void printEnds(myQueue queue) {
        System.out.println(queue.getFront() + " " + queue.getRear());
    }
}
