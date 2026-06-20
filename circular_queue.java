import java.util.NoSuchElementException;

class CircularQueue {

    private final int[] arr;
    private int front;
    private int size;
    private final int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.arr = new int[capacity];
        this.front = 0;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }

    public void enqueue(int x) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        int rear = (front + size) % capacity;
        arr[rear] = x;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        int value = arr[front];
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    public int getFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return arr[front];
    }

    public int getRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return arr[(front + size - 1) % capacity];
    }

    public static void main(String[] args) {
        CircularQueue q = new CircularQueue(5);
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
