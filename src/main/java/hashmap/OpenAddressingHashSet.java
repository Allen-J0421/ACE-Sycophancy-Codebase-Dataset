package hashmap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} built on the same open-addressing core as {@link OpenAddressingHashMap}.
 *
 * <p>Both collections compose the package-private {@link OpenAddressingTable}, which
 * holds the shared storage, probing, and resizing logic. The set constructs its
 * table with value-tracking <em>disabled</em>, so &mdash; unlike a set that merely
 * wraps a map with a dummy value (the {@code java.util.HashSet} approach) &mdash;
 * it allocates <strong>no</strong> per-element value storage at all.
 *
 * <p>Extending {@link AbstractSet} supplies the bulk operations
 * ({@code equals}/{@code hashCode}/{@code addAll}/{@code removeAll}/
 * {@code retainAll}/{@code toArray}/{@code toString}) in terms of the primitives
 * overridden here ({@link #add}, {@link #remove(Object)}, {@link #contains(Object)},
 * {@link #iterator()}, {@link #size()}).
 *
 * <p>Iteration is in an unspecified order and is <em>fail-fast</em>. Like the core,
 * this set does <strong>not</strong> permit {@code null} elements &mdash;
 * {@link #add} throws {@link NullPointerException} &mdash; and is
 * <strong>not</strong> thread-safe.
 *
 * @param <E> the type of elements maintained by this set
 */
public final class OpenAddressingHashSet<E> extends AbstractSet<E> {

    private final OpenAddressingTable table;

    /** Creates an empty set with the core's default capacity and load factor. */
    public OpenAddressingHashSet() {
        this.table = new OpenAddressingTable(
            OpenAddressingTable.DEFAULT_CAPACITY, OpenAddressingTable.DEFAULT_LOAD_FACTOR, false);
    }

    /**
     * Creates an empty set with the given initial capacity.
     *
     * @param initialCapacity a hint for the number of elements to hold without resizing
     * @throws IllegalArgumentException if {@code initialCapacity} is not positive
     */
    public OpenAddressingHashSet(int initialCapacity) {
        this.table = new OpenAddressingTable(
            initialCapacity, OpenAddressingTable.DEFAULT_LOAD_FACTOR, false);
    }

    /**
     * Creates an empty set with the given initial capacity and load factor.
     *
     * @param initialCapacity a hint for the number of elements to hold without resizing
     * @param loadFactor      the fraction of occupied slots that triggers a resize,
     *                        in the open interval {@code (0, 1)}
     * @throws IllegalArgumentException if the arguments are out of range
     */
    public OpenAddressingHashSet(int initialCapacity, double loadFactor) {
        this.table = new OpenAddressingTable(initialCapacity, loadFactor, false);
    }

    /**
     * Creates a set containing the distinct elements of {@code source}, sized to
     * hold them without resizing.
     *
     * @param source the elements to copy
     * @throws NullPointerException if {@code source} is {@code null}, or contains a null element
     */
    public OpenAddressingHashSet(Collection<? extends E> source) {
        this.table = new OpenAddressingTable(
            Math.max(OpenAddressingTable.DEFAULT_CAPACITY, source.size() * 2 + 1),
            OpenAddressingTable.DEFAULT_LOAD_FACTOR, false);
        addAll(source);
    }

    /**
     * Adds {@code element} if not already present.
     *
     * @param element the element to add (must not be {@code null})
     * @return {@code true} if the set did not already contain the element
     * @throws NullPointerException if {@code element} is {@code null}
     */
    @Override
    public boolean add(E element) {
        return table.insert(element) < 0; // negative result == newly inserted
    }

    @Override
    public boolean remove(Object o) {
        int index = table.indexOf(o);
        if (index < 0) {
            return false;
        }
        table.deleteAt(index);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return table.indexOf(o) >= 0;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public void clear() {
        table.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return new ElementIterator();
    }

    /** Fail-fast iterator over elements, delegating slot walking to the table cursor. */
    private final class ElementIterator implements Iterator<E> {
        private final OpenAddressingTable.Cursor cursor = table.cursor();

        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            return (E) table.keyAt(cursor.nextIndex());
        }

        @Override
        public void remove() {
            cursor.remove();
        }
    }
}
