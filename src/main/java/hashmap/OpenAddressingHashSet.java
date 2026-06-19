package hashmap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} backed by an {@link OpenAddressingHashMap}, in the same way
 * {@link java.util.HashSet} is backed by a {@link java.util.HashMap}: each element
 * is stored as a key mapped to a shared sentinel value. This reuses all of the
 * map's machinery (open addressing, linear probing, struct-of-arrays storage,
 * resizing) so the set is a thin, correct adapter rather than a parallel
 * reimplementation.
 *
 * <p>Extending {@link AbstractSet} supplies the bulk operations
 * ({@code equals}/{@code hashCode}/{@code addAll}/{@code removeAll}/
 * {@code retainAll}/{@code toArray}/{@code toString}) in terms of the four
 * primitives overridden here ({@link #add}, {@link #remove(Object)},
 * {@link #contains(Object)}, {@link #iterator()}, {@link #size()}).
 *
 * <p>Iteration is in an unspecified order and is <em>fail-fast</em> (it delegates
 * to the backing map's {@code keySet()} iterator, which supports {@code remove}).
 * Like the backing map, this set does <strong>not</strong> permit {@code null}
 * elements &mdash; {@link #add} throws {@link NullPointerException} &mdash; and is
 * <strong>not</strong> thread-safe.
 *
 * @param <E> the type of elements maintained by this set
 */
public final class OpenAddressingHashSet<E> extends AbstractSet<E> {

    /** Dummy value associated with every element key in the backing map. */
    private static final Object PRESENT = new Object();

    private final OpenAddressingHashMap<E, Object> map;

    /** Creates an empty set with the backing map's default capacity and load factor. */
    public OpenAddressingHashSet() {
        this.map = new OpenAddressingHashMap<>();
    }

    /**
     * Creates an empty set with the given initial capacity.
     *
     * @param initialCapacity a hint for the number of elements to hold without resizing
     * @throws IllegalArgumentException if {@code initialCapacity} is not positive
     */
    public OpenAddressingHashSet(int initialCapacity) {
        this.map = new OpenAddressingHashMap<>(initialCapacity);
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
        this.map = new OpenAddressingHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Creates a set containing the distinct elements of {@code source}, sized to
     * hold them without resizing.
     *
     * @param source the elements to copy
     * @throws NullPointerException if {@code source} is {@code null}, or contains a null element
     */
    public OpenAddressingHashSet(Collection<? extends E> source) {
        this.map = new OpenAddressingHashMap<>(Math.max(16, source.size() * 2 + 1));
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
        return map.put(element, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
}
