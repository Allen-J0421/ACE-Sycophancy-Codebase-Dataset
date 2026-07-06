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

    public static void main(String[] args) {
        IntQueue q = new CircularQueue(5);
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
