import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CircularQueue<T> implements Iterable<T> {

    private final Object[] arr;
    private int front;
    private int size;
    private final int capacity;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
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
        T value = elementAt(0);
        arr[front] = null;
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return elementAt(0);
    }

    public T peekRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return elementAt(size - 1);
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int logicalIndex) {
        return (T) arr[(front + logicalIndex) % capacity];
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            arr[(front + i) % capacity] = null;
        }
        front = 0;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }

    private final class QueueIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elementAt(index++);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CircularQueue)) return false;
        CircularQueue<?> other = (CircularQueue<?>) o;
        if (size != other.size) return false;
        Iterator<T> it1 = iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext()) {
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (T item : this) {
            result = 31 * result + Objects.hashCode(item);
        }
        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("Failed: " + description);
    }

    public static void main(String[] args) {
        // enqueue and peek
        CircularQueue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        check(q.peek() == 10,    "front after 3 enqueues");
        check(q.peekRear() == 30, "rear after 3 enqueues");
        check(q.size() == 3,     "size after 3 enqueues");

        // dequeue and wrap-around enqueue
        check(q.dequeue() == 10, "dequeue returns front");
        check(q.peek() == 20,    "front after dequeue");
        q.enqueue(40);
        check(q.peekRear() == 40, "rear after wrap-around enqueue");

        // iteration order
        int[] expected = {20, 30, 40};
        int i = 0;
        for (int item : q) {
            check(item == expected[i++], "iteration order at index " + (i - 1));
        }

        // toString
        check(q.toString().equals("[20, 30, 40]"), "toString");

        // equals and hashCode
        CircularQueue<Integer> q2 = new CircularQueue<>(5);
        q2.enqueue(10);
        q2.enqueue(20);
        CircularQueue<Integer> q3 = new CircularQueue<>(5);
        q3.enqueue(10);
        q3.enqueue(20);
        check(q2.equals(q3),                    "equal queues");
        check(q2.hashCode() == q3.hashCode(),   "equal hashCodes");

        // clear
        q.clear();
        check(q.isEmpty(),              "isEmpty after clear");
        check(q.toString().equals("[]"), "toString after clear");

        // invalid capacity
        try {
            new CircularQueue<>(0);
            check(false, "should throw on capacity 0");
        } catch (IllegalArgumentException ignored) {}

        System.out.println("All checks passed.");
    }
}
