import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A fixed-capacity, generic {@link java.util.Deque} backed by a circular (ring)
 * buffer.
 *
 * <p>Elements can be inserted, removed, and examined at both ends in O(1) time.
 * The backing array is allocated once at construction and reused; the
 * {@code front} index and {@code size} are advanced modulo the capacity so the
 * common end operations never shift elements. (Arbitrary-element removal does
 * shift, in O(n), as in {@link java.util.ArrayDeque}.)
 *
 * <p>As a {@code Deque} this class is a superset of a FIFO queue and a LIFO
 * stack. The two operation styles behave as the interface specifies:
 * <ul>
 *   <li>{@code offer*} / {@code poll*} / {@code peek*} report failure by
 *       returning {@code false}/{@code null} (capacity-safe).</li>
 *   <li>{@code add*} / {@code remove*} / {@code get*} report failure by throwing
 *       {@link IllegalStateException} (full) or {@link NoSuchElementException}
 *       (empty).</li>
 * </ul>
 *
 * <p><strong>Null elements are not permitted.</strong> The contract uses
 * {@code null} as the sentinel returned by {@code poll}/{@code peek} for an
 * empty deque, so insertion methods throw {@link NullPointerException} for a
 * null argument, consistent with {@link java.util.ArrayDeque}.
 *
 * <p>The {@link #iterator()} and {@link #descendingIterator()} are fail-fast and
 * support {@link Iterator#remove()}, so the optional bulk operations
 * ({@code removeAll}, {@code retainAll}, {@code removeIf}) work as well.
 *
 * <p>This class is not thread-safe. External synchronization is required if
 * instances are accessed concurrently.
 *
 * @param <E> the type of elements held in this deque
 */
public class CircularDeque<E> extends AbstractQueue<E> implements Deque<E> {

    private final Object[] elements;
    private int front;
    private int size;

    /** Tracks structural modifications so iterators can fail fast. */
    private int modCount;

    /**
     * Creates an empty deque able to hold up to {@code capacity} elements.
     *
     * @param capacity the maximum number of elements; must be positive
     * @throws IllegalArgumentException if {@code capacity <= 0}
     */
    public CircularDeque(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, was " + capacity);
        }
        this.elements = new Object[capacity];
        this.front = 0;
        this.size = 0;
    }

    /** Returns the number of elements currently in the deque. */
    @Override
    public int size() {
        return size;
    }

    /** Returns the maximum number of elements the deque can hold. */
    public int capacity() {
        return elements.length;
    }

    /** Returns {@code true} if the deque cannot hold any more elements. */
    public boolean isFull() {
        return size == elements.length;
    }

    // --- insertion --------------------------------------------------------

    /**
     * Inserts the element at the front if space is available.
     *
     * @return {@code true} if added, {@code false} if the deque is full
     * @throws NullPointerException if {@code element} is null
     */
    @Override
    public boolean offerFirst(E element) {
        Objects.requireNonNull(element, "null elements are not permitted");
        if (isFull()) {
            return false;
        }
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = element;
        size++;
        modCount++;
        return true;
    }

    /**
     * Inserts the element at the rear if space is available.
     *
     * @return {@code true} if added, {@code false} if the deque is full
     * @throws NullPointerException if {@code element} is null
     */
    @Override
    public boolean offerLast(E element) {
        Objects.requireNonNull(element, "null elements are not permitted");
        if (isFull()) {
            return false;
        }
        elements[(front + size) % elements.length] = element;
        size++;
        modCount++;
        return true;
    }

    /** Inserts the element at the front, or throws if the deque is full. */
    @Override
    public void addFirst(E element) {
        if (!offerFirst(element)) {
            throw new IllegalStateException("Deque is full (capacity " + elements.length + ")");
        }
    }

    /** Inserts the element at the rear, or throws if the deque is full. */
    @Override
    public void addLast(E element) {
        if (!offerLast(element)) {
            throw new IllegalStateException("Deque is full (capacity " + elements.length + ")");
        }
    }

    /** {@inheritDoc} Equivalent to {@link #offerLast(Object)}. */
    @Override
    public boolean offer(E element) {
        return offerLast(element);
    }

    // --- removal ----------------------------------------------------------

    /**
     * Removes and returns the front element.
     *
     * @return the former front element, or {@code null} if the deque is empty
     */
    @Override
    public E pollFirst() {
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
     * Removes and returns the rear element.
     *
     * @return the former rear element, or {@code null} if the deque is empty
     */
    @Override
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        int rear = (front + size - 1) % elements.length;
        E element = elementAt(rear);
        elements[rear] = null;
        size--;
        modCount++;
        return element;
    }

    /** Removes and returns the front element, or throws if empty. */
    @Override
    public E removeFirst() {
        return nonNullOrThrow(pollFirst());
    }

    /** Removes and returns the rear element, or throws if empty. */
    @Override
    public E removeLast() {
        return nonNullOrThrow(pollLast());
    }

    /** {@inheritDoc} Equivalent to {@link #pollFirst()}. */
    @Override
    public E poll() {
        return pollFirst();
    }

    // --- examination ------------------------------------------------------

    /**
     * Returns, without removing, the front element.
     *
     * @return the front element, or {@code null} if the deque is empty
     */
    @Override
    public E peekFirst() {
        return isEmpty() ? null : elementAt(front);
    }

    /**
     * Returns, without removing, the rear element.
     *
     * @return the rear element, or {@code null} if the deque is empty
     */
    @Override
    public E peekLast() {
        return isEmpty() ? null : elementAt((front + size - 1) % elements.length);
    }

    /** Returns the front element, or throws if empty. */
    @Override
    public E getFirst() {
        return nonNullOrThrow(peekFirst());
    }

    /** Returns the rear element, or throws if empty. */
    @Override
    public E getLast() {
        return nonNullOrThrow(peekLast());
    }

    /** {@inheritDoc} Equivalent to {@link #peekFirst()}. */
    @Override
    public E peek() {
        return peekFirst();
    }

    // --- stack view -------------------------------------------------------

    /** {@inheritDoc} Equivalent to {@link #addFirst(Object)}. */
    @Override
    public void push(E element) {
        addFirst(element);
    }

    /** {@inheritDoc} Equivalent to {@link #removeFirst()}. */
    @Override
    public E pop() {
        return removeFirst();
    }

    // --- queue aliases (domain vocabulary) --------------------------------

    /**
     * Convenience alias for {@link #addLast(Object)}.
     *
     * @throws IllegalStateException if the deque is full
     * @throws NullPointerException if {@code element} is null
     */
    public void enqueue(E element) {
        addLast(element);
    }

    /**
     * Convenience alias for {@link #removeFirst()}.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public E dequeue() {
        return removeFirst();
    }

    // --- occurrence removal ----------------------------------------------

    /** {@inheritDoc} Removes the first (front-most) element equal to {@code o}. */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, getLogical(i))) {
                deleteAtLogical(i);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} Removes the last (rear-most) element equal to {@code o}. */
    @Override
    public boolean removeLastOccurrence(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(o, getLogical(i))) {
                deleteAtLogical(i);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} Equivalent to {@link #removeFirstOccurrence(Object)}. */
    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    /** Removes all elements, leaving the deque empty but with unchanged capacity. */
    @Override
    public void clear() {
        Arrays.fill(elements, null);
        front = 0;
        size = 0;
        modCount++;
    }

    // --- iteration --------------------------------------------------------

    /**
     * Returns a fail-fast iterator over the elements from front to rear.
     * Supports {@link Iterator#remove()}.
     */
    @Override
    public Iterator<E> iterator() {
        return new AscendingIterator();
    }

    /**
     * Returns a fail-fast iterator over the elements from rear to front.
     * Supports {@link Iterator#remove()}.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    private final class AscendingIterator implements Iterator<E> {
        private int cursor = 0;
        private int lastReturned = -1;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public E next() {
            checkForComodification();
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            lastReturned = cursor;
            E element = getLogical(cursor);
            cursor++;
            return element;
        }

        @Override
        public void remove() {
            if (lastReturned < 0) {
                throw new IllegalStateException("next() has not been called, or remove() already called");
            }
            checkForComodification();
            deleteAtLogical(lastReturned);
            // Remaining elements shifted down into the gap; resume from the gap.
            cursor = lastReturned;
            lastReturned = -1;
            expectedModCount = modCount;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private final class DescendingIterator implements Iterator<E> {
        private int cursor = size - 1;
        private int lastReturned = -1;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor >= 0;
        }

        @Override
        public E next() {
            checkForComodification();
            if (cursor < 0) {
                throw new NoSuchElementException();
            }
            lastReturned = cursor;
            E element = getLogical(cursor);
            cursor--;
            return element;
        }

        @Override
        public void remove() {
            if (lastReturned < 0) {
                throw new IllegalStateException("next() has not been called, or remove() already called");
            }
            checkForComodification();
            deleteAtLogical(lastReturned);
            // Only elements after the gap shift; those still to visit (below the
            // cursor) keep their logical indices, so the cursor is unchanged.
            lastReturned = -1;
            expectedModCount = modCount;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    // --- internals --------------------------------------------------------

    /** Returns the element at logical position {@code i} (0 == front). */
    private E getLogical(int i) {
        return elementAt((front + i) % elements.length);
    }

    /** Sets the element at logical position {@code i} (0 == front). */
    private void setLogical(int i, E value) {
        elements[(front + i) % elements.length] = value;
    }

    /**
     * Removes the element at logical position {@code k}, closing the gap by
     * shifting whichever side is smaller, and advances {@code front} when the
     * head side moves. Order of the remaining elements is preserved.
     */
    private void deleteAtLogical(int k) {
        if (k < size - 1 - k) {
            // Closer to the front: slide logical [0..k-1] up into [1..k].
            for (int i = k; i > 0; i--) {
                setLogical(i, getLogical(i - 1));
            }
            setLogical(0, null);
            front = (front + 1) % elements.length;
        } else {
            // Closer to the rear: slide logical [k+1..size-1] down into [k..size-2].
            for (int i = k; i < size - 1; i++) {
                setLogical(i, getLogical(i + 1));
            }
            setLogical(size - 1, null);
        }
        size--;
        modCount++;
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int index) {
        return (E) elements[index];
    }

    private E nonNullOrThrow(E element) {
        if (element == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return element;
    }

    // --- value equality (extension beyond the Collection contract) --------

    /**
     * Compares deques by logical contents and order, front to rear. Two deques
     * are equal if they hold equal elements in the same order, regardless of
     * capacity or internal buffer alignment.
     *
     * <p>This is an intentional value-equality extension beyond the
     * {@code Collection} contract (which leaves {@code equals} as identity for
     * deques); it only ever considers another {@code CircularDeque} equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CircularDeque<?> other)) {
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
