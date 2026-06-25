class myQueue {

    private final int[] elements;
    private final int capacity;
    private int front;
    private int size;

    public myQueue(int cap) {
        capacity = cap;
        elements = new int[capacity];
        front = 0;
        size = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            return;
        }
        elements[tailIndex()] = value;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            return -1;
        }
        int removed = elements[front];
        elements[front] = 0;
        front = nextIndex(front);
        size--;
        return removed;
    }

    public int getFront() {
        return isEmpty() ? -1 : elements[front];
    }

    public int getRear() {
        return isEmpty() ? -1 : elements[rearIndex()];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int capacity() {
        return capacity;
    }

    private int tailIndex() {
        return (front + size) % capacity;
    }

    private int rearIndex() {
        return (front + size - 1 + capacity) % capacity;
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity;
    }

    public static void main(String[] args) {
        myQueue queue = new myQueue(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.getFront() + " " + queue.getRear());
        queue.dequeue();
        System.out.println(queue.getFront() + " " + queue.getRear());
        queue.enqueue(40);
        System.out.println(queue.getFront() + " " + queue.getRear());
    }
}
