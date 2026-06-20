import java.util.NoSuchElementException;

class CircularQueue<T> {

    private final Object[] arr;
    private int front;
    private int size;
    private final int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.arr = new Object[capacity];
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

    public void enqueue(T item) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        arr[(front + size) % capacity] = item;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        @SuppressWarnings("unchecked")
        T value = (T) arr[front];
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        @SuppressWarnings("unchecked")
        T value = (T) arr[front];
        return value;
    }

    public T peekRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        @SuppressWarnings("unchecked")
        T value = (T) arr[(front + size - 1) % capacity];
        return value;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(arr[(front + i) % capacity]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        CircularQueue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        System.out.println(q + " front=" + q.peek() + " rear=" + q.peekRear());
        q.dequeue();
        System.out.println(q + " front=" + q.peek() + " rear=" + q.peekRear());
        q.enqueue(40);
        System.out.println(q + " front=" + q.peek() + " rear=" + q.peekRear());
    }
}
