class myQueue {

    private final int[] elements;

    private int front;

    private int size;

    public myQueue(int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive");
        }

        elements = new int[cap];
        front = 0;
        size = 0;
    }

    public void enqueue(int x) {
        if (isFull()) {
            System.out.println("Queue is full!");
            return;
        }

        elements[nextRearIndex()] = x;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return -1;
        }

        int value = elements[front];
        front = (front + 1) % capacity();
        size--;
        return value;
    }

    public int getFront() {
        if (isEmpty()) {
            return -1;
        }

        return elements[front];
    }

    public int getRear() {
        if (isEmpty()) {
            return -1;
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
        myQueue q = new myQueue(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        System.out.println(q.getFront() + " " + q.getRear());
        q.dequeue();
        System.out.println(q.getFront() + " " + q.getRear());
        q.enqueue(40);
        System.out.println(q.getFront() + " " + q.getRear());
    }
}
