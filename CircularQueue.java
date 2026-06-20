import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A fixed-capacity, generic FIFO {@link java.util.Queue} backed by a circular
 * (ring) buffer.
 *
 * <p>Elements are added at the rear and removed from the front in O(1) time.
 * The backing array is allocated once at construction and reused; the
 * {@code front} index and {@code size} are advanced modulo the capacity so no
 * element is ever shifted.
 *
 * <p>By extending {@link AbstractQueue}, this class supports the full
 * {@code Queue} API. The two insertion/removal styles behave as the interface
 * specifies:
 * <ul>
 *   <li>{@link #offer(Object)} / {@link #poll()} / {@link #peek()} report
 *       failure by returning {@code false}/{@code null} (capacity-safe).</li>
 *   <li>{@link #add(Object)} / {@link #remove()} / {@link #element()} report
 *       failure by throwing {@link IllegalStateException} (full) or
 *       {@link NoSuchElementException} (empty).</li>
 * </ul>
 *
 * <p><strong>Null elements are not permitted.</strong> The {@code Queue}
 * contract uses {@code null} as the sentinel returned by {@code poll}/{@code peek}
 * for an empty queue, so permitting null elements would make those methods
 * ambiguous; {@link #offer(Object)} throws {@link NullPointerException} for a
 * null argument, consistent with {@link java.util.ArrayDeque}.
 *
 * <p>Arbitrary-element removal (e.g. {@code remove(Object)}, {@code removeAll})
 * is unsupported, as it would violate FIFO ordering; the iterator does not
 * support {@link Iterator#remove()}. Use {@link #poll()}/{@link #remove()} to
 * remove from the front.
 *
 * <p>This class is not thread-safe. External synchronization is required if
 * instances are accessed concurrently.
 *
 * @param <E> the type of elements held in this queue
 */
public class CircularQueue<E> extends AbstractQueue<E> {

    private final Object[] elements;
    private int front;
    private int size;

    /** Tracks structural modifications so iterators can fail fast. */
    private int modCount;

    /**
     * Creates an empty queue able to hold up to {@code capacity} elements.
     *
     * @param capacity the maximum number of elements; must be positive
     * @throws IllegalArgumentException if {@code capacity <= 0}
     */
    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, was " + capacity);
        }
        this.elements = new Object[capacity];
        this.front = 0;
        this.size = 0;
    }

    /** Returns the number of elements currently in the queue. */
    @Override
    public int size() {
        return size;
    }

    /** Returns the maximum number of elements the queue can hold. */
    public int capacity() {
        return elements.length;
    }

    /** Returns {@code true} if the queue cannot hold any more elements. */
    public boolean isFull() {
        return size == elements.length;
    }

    /**
     * Inserts the given element at the rear if space is available.
     *
     * @param element the element to add
     * @return {@code true} if the element was added, {@code false} if the queue is full
     * @throws NullPointerException if {@code element} is null
     */
    @Override
    public boolean offer(E element) {
        Objects.requireNonNull(element, "null elements are not permitted");
        if (isFull()) {
            return false;
        }
        int rear = (front + size) % elements.length;
        elements[rear] = element;
        size++;
        modCount++;
        return true;
    }

    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return the former front element, or {@code null} if the queue is empty
     */
    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        E element = elementAt(front);
        elements[front] = null; // let the removed element be garbage collected
        front = (front + 1) % elements.length;
        size--;
        modCount++;
        return element;
    }

    /**
     * Returns, without removing, the element at the front of the queue.
     *
     * @return the front element, or {@code null} if the queue is empty
     */
    @Override
    public E peek() {
        return isEmpty() ? null : elementAt(front);
    }

    /**
     * Returns, without removing, the element at the rear of the queue.
     *
     * @return the rear element, or {@code null} if the queue is empty
     */
    public E peekRear() {
        return isEmpty() ? null : elementAt((front + size - 1) % elements.length);
    }

    /**
     * Convenience alias for {@link #add(Object)}: inserts at the rear, throwing
     * {@link IllegalStateException} if the queue is full.
     *
     * @param element the element to add
     * @throws IllegalStateException if the queue is full
     * @throws NullPointerException if {@code element} is null
     */
    public void enqueue(E element) {
        add(element);
    }

    /**
     * Convenience alias for {@link #remove()}: removes and returns the front
     * element, throwing {@link NoSuchElementException} if the queue is empty.
     *
     * @return the former front element
     * @throws NoSuchElementException if the queue is empty
     */
    public E dequeue() {
        return remove();
    }

    /** Removes all elements, leaving the queue empty but with unchanged capacity. */
    @Override
    public void clear() {
        Arrays.fill(elements, null);
        front = 0;
        size = 0;
        modCount++;
    }

    /**
     * Returns an iterator over the elements from front to rear.
     *
     * <p>The iterator is fail-fast: if the queue is structurally modified after
     * the iterator is created, it throws {@link ConcurrentModificationException}.
     * It does not support {@link Iterator#remove()}.
     */
    @Override
    public Iterator<E> iterator() {
        return new QueueIterator();
    }

    private final class QueueIterator implements Iterator<E> {
        private int visited = 0;
        private final int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return visited < size;
        }

        @Override
        public E next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E element = elementAt((front + visited) % elements.length);
            visited++;
            return element;
        }
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int index) {
        return (E) elements[index];
    }

    /**
     * Compares queues by logical contents and order, front to rear. Two queues
     * are equal if they hold equal elements in the same FIFO order, regardless
     * of capacity or internal buffer alignment.
     *
     * <p>This is an intentional value-equality extension beyond the
     * {@code Collection} contract (which leaves {@code equals} as identity for
     * queues); it only ever considers another {@code CircularQueue} equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CircularQueue<?> other)) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        Iterator<E> a = iterator();
        Iterator<?> b = other.iterator();
        while (a.hasNext()) {
            if (!Objects.equals(a.next(), b.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for (E element : this) {
            hash = 31 * hash + element.hashCode();
        }
        return hash;
    }
}
