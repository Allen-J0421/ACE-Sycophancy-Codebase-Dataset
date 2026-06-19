package hashmap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Map} implemented with <em>open addressing</em>, <em>linear probing</em>,
 * and a <em>struct-of-arrays</em> storage layout.
 *
 * <p>Rather than storing one wrapper object per entry, the table is held as two
 * parallel arrays &mdash; {@code keys} and {@code values}. This removes a level of
 * indirection (there is no per-entry node object to allocate or dereference) and
 * improves locality: a probe scans the contiguous {@code keys} array and only
 * touches {@code values} once a key actually matches. A slot's state is encoded in
 * the {@code keys} array itself: {@code null} marks an unused slot, a private
 * {@link #TOMBSTONE} sentinel marks a deleted slot, and any other reference marks a
 * live entry. Because occupancy is determined entirely by {@code keys}, a live
 * entry may freely have a {@code null} value.
 *
 * <p>Keys and values are generic. {@code null} keys are not permitted; {@code null}
 * values are. Collisions are resolved by scanning forward (with wrap-around) to the
 * next free slot. Tombstones keep probe sequences intact; they are reclaimed during
 * insertion and cleared on the next resize. The table length is always a power of
 * two, so the slot index is a bit-mask rather than a modulo, and the table grows
 * once the fraction of occupied slots (live entries plus tombstones) exceeds the
 * configured load factor, keeping {@code get}/{@code put}/{@code remove} amortized
 * {@code O(1)}.
 *
 * <p>This class extends {@link AbstractMap} so that it inherits the standard
 * {@code Map} contracts for {@link #equals}, {@link #hashCode}, {@link #toString},
 * {@link #putAll}, {@link #keySet}, and {@link #values}, while overriding the
 * hot-path operations with constant-time implementations. The {@link #entrySet()}
 * view, its iterator, and the derived key/value views are <em>fail-fast</em>:
 * structurally modifying the map during iteration (other than through the
 * iterator's own {@code remove}) throws {@link ConcurrentModificationException}.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public final class OpenAddressingHashMap<K, V> extends AbstractMap<K, V> {

    /** Default initial capacity; must be a power of two. */
    private static final int DEFAULT_CAPACITY = 16;

    /** Default fraction of occupied slots that triggers a resize. */
    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    /** Sentinel stored in {@code keys} to mark a deleted slot, distinct from {@code null}. */
    private static final Object TOMBSTONE = new Object();

    /** Parallel storage: {@code keys[i]} and {@code values[i]} form one entry. */
    private Object[] keys;
    private Object[] values;

    /** Number of live (non-tombstone) entries. */
    private int size;

    /** Live entries plus tombstones; the table is resized based on this figure. */
    private int occupiedSlots;

    /** Structural-modification counter backing fail-fast iteration. */
    private int modCount;

    private final double loadFactor;

    /** Creates an empty map with the default capacity and load factor. */
    public OpenAddressingHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates an empty map with the given initial capacity and the default load factor.
     *
     * @param initialCapacity a hint for the number of entries to hold without resizing
     * @throws IllegalArgumentException if {@code initialCapacity} is not positive
     */
    public OpenAddressingHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates an empty map with the given initial capacity and load factor.
     *
     * @param initialCapacity a hint for the number of entries to hold without resizing
     * @param loadFactor      the fraction of occupied slots that triggers a resize,
     *                        in the open interval {@code (0, 1)}
     * @throws IllegalArgumentException if {@code initialCapacity} is not positive
     *                                  or {@code loadFactor} is not in {@code (0, 1)}
     */
    public OpenAddressingHashMap(int initialCapacity, double loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be positive: " + initialCapacity);
        }
        if (!(loadFactor > 0.0 && loadFactor < 1.0) || Double.isNaN(loadFactor)) {
            throw new IllegalArgumentException("loadFactor must be in (0, 1): " + loadFactor);
        }
        int capacity = tableSizeFor(initialCapacity);
        this.keys = new Object[capacity];
        this.values = new Object[capacity];
        this.loadFactor = loadFactor;
    }

    /**
     * Creates a map containing the mappings of {@code source}, sized to hold them
     * without resizing.
     *
     * @param source the mappings to copy
     * @throws NullPointerException if {@code source} is {@code null}, or contains a null key
     */
    public OpenAddressingHashMap(Map<? extends K, ? extends V> source) {
        this(Math.max(DEFAULT_CAPACITY, (int) (source.size() / DEFAULT_LOAD_FACTOR) + 1), DEFAULT_LOAD_FACTOR);
        putAll(source);
    }

    // ----------------------------------------------------------------------
    // Constant-time overrides of the AbstractMap defaults
    // ----------------------------------------------------------------------

    /**
     * Associates {@code value} with {@code key}, replacing any previous mapping.
     *
     * @param key   the key, which must not be {@code null}
     * @param value the value to associate (may be {@code null})
     * @return the previous value for {@code key}, or {@code null} if there was none
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");

        if (occupiedSlots + 1 > keys.length * loadFactor) {
            resize(keys.length * 2);
        }

        int firstTombstone = -1;
        int index = indexFor(key, keys.length);
        while (true) {
            Object k = keys[index];
            if (k == null) {
                // Reuse the earliest tombstone seen on the probe path, if any.
                int target = (firstTombstone >= 0) ? firstTombstone : index;
                if (firstTombstone < 0) {
                    occupiedSlots++;
                }
                keys[target] = key;
                values[target] = value;
                size++;
                modCount++;
                return null;
            }
            if (k == TOMBSTONE) {
                if (firstTombstone < 0) {
                    firstTombstone = index;
                }
            } else if (k.equals(key)) {
                V previous = (V) values[index];
                values[index] = value;
                return previous; // value replacement is not a structural modification
            }
            index = next(index);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int index = findIndex(key);
        return index < 0 ? null : (V) values[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getOrDefault(Object key, V defaultValue) {
        int index = findIndex(key);
        return index < 0 ? defaultValue : (V) values[index];
    }

    @Override
    public boolean containsKey(Object key) {
        return findIndex(key) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        if (key == null) {
            return null;
        }
        int index = indexFor(key, keys.length);
        while (true) {
            Object k = keys[index];
            if (k == null) {
                return null;
            }
            if (k != TOMBSTONE && k.equals(key)) {
                V previous = (V) values[index];
                keys[index] = TOMBSTONE;
                values[index] = null; // release the value for GC
                size--;
                modCount++;
                // occupiedSlots is unchanged: the tombstone still occupies the slot.
                return previous;
            }
            index = next(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        // Allocate fresh storage so capacity is not bloated by a transient large size.
        keys = new Object[DEFAULT_CAPACITY];
        values = new Object[DEFAULT_CAPACITY];
        size = 0;
        occupiedSlots = 0;
        modCount++;
    }

    /**
     * Returns a live, fail-fast {@link Set} view of the mappings. The view supports
     * element removal (which removes the mapping from the map) but not addition.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    // ----------------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------------

    /** Returns the index of the live slot for {@code key}, or {@code -1} if absent. */
    private int findIndex(Object key) {
        if (key == null) {
            return -1;
        }
        int index = indexFor(key, keys.length);
        while (true) {
            Object k = keys[index];
            if (k == null) {
                return -1;
            }
            if (k != TOMBSTONE && k.equals(key)) {
                return index;
            }
            index = next(index);
        }
    }

    /** Returns whether slot {@code i} holds a live entry. */
    private boolean isLive(int i) {
        Object k = keys[i];
        return k != null && k != TOMBSTONE;
    }

    /** Computes the home slot for {@code key} in a table of {@code length} slots. */
    private static int indexFor(Object key, int length) {
        int h = key.hashCode();
        h ^= (h >>> 16); // spread high bits into low bits to reduce clustering
        return h & (length - 1);
    }

    /** Returns the next slot index, wrapping around the power-of-two table. */
    private int next(int index) {
        return (index + 1) & (keys.length - 1);
    }

    /** Rebuilds the table at {@code newCapacity}, dropping tombstones. */
    private void resize(int newCapacity) {
        Object[] oldKeys = keys;
        Object[] oldValues = values;
        keys = new Object[newCapacity];
        values = new Object[newCapacity];
        occupiedSlots = 0;
        // size is unchanged: every live slot is re-placed exactly once.
        for (int i = 0; i < oldKeys.length; i++) {
            Object k = oldKeys[i];
            if (k != null && k != TOMBSTONE) {
                placeKnownAbsent(k, oldValues[i]);
                occupiedSlots++;
            }
        }
    }

    /**
     * Places an entry known to be unique into the current table. Used only by
     * {@link #resize}, where there are no duplicates and no tombstones, so the
     * first {@code null} encountered is the destination.
     */
    private void placeKnownAbsent(Object key, Object value) {
        int index = indexFor(key, keys.length);
        while (keys[index] != null) {
            index = next(index);
        }
        keys[index] = key;
        values[index] = value;
    }

    /** Rounds {@code cap} up to the nearest power of two (at least 1). */
    private static int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : n + 1;
    }

    // ----------------------------------------------------------------------
    // Views
    // ----------------------------------------------------------------------

    /**
     * A {@link Map.Entry} backed by a table index. {@code getValue}/{@code setValue}
     * read and write {@code values[index]} directly, so the entry stays in sync with
     * the map (writes are visible to the map, per the {@code Map.Entry} contract).
     * The key is captured at construction, since a live slot's key never changes
     * while the slot remains live.
     */
    private final class IndexEntry implements Map.Entry<K, V> {
        private final int index;
        private final K key;

        @SuppressWarnings("unchecked")
        IndexEntry(int index) {
            this.index = index;
            this.key = (K) keys[index];
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) values[index];
        }

        @Override
        @SuppressWarnings("unchecked")
        public V setValue(V newValue) {
            V old = (V) values[index];
            values[index] = newValue;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            }
            return Objects.equals(key, e.getKey()) && Objects.equals(getValue(), e.getValue());
        }

        @Override
        public int hashCode() {
            V value = getValue();
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
    }

    /** The {@code entrySet} view; key and value views are derived from it by AbstractMap. */
    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            OpenAddressingHashMap.this.clear();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            }
            int index = findIndex(e.getKey());
            return index >= 0 && Objects.equals(values[index], e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            }
            int index = findIndex(e.getKey());
            if (index >= 0 && Objects.equals(values[index], e.getValue())) {
                OpenAddressingHashMap.this.remove(e.getKey());
                return true;
            }
            return false;
        }
    }

    /** Fail-fast iterator over live slots, supporting {@link #remove()}. */
    private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int expectedModCount = modCount;
        private int scan;            // next table index to examine
        private int nextIndex = -1;  // table index of the next live slot, or -1
        private int lastIndex = -1;  // table index of the last returned slot, for remove()

        EntryIterator() {
            advance();
        }

        private void advance() {
            while (scan < keys.length) {
                if (isLive(scan)) {
                    nextIndex = scan++;
                    return;
                }
                scan++;
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (nextIndex < 0) {
                throw new NoSuchElementException();
            }
            lastIndex = nextIndex;
            Map.Entry<K, V> entry = new IndexEntry(lastIndex);
            advance();
            return entry;
        }

        @Override
        public void remove() {
            if (lastIndex < 0) {
                throw new IllegalStateException("next() has not been called, or remove() already called");
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            keys[lastIndex] = TOMBSTONE;
            values[lastIndex] = null;
            size--;
            modCount++;
            expectedModCount = modCount; // stay consistent so iteration may continue
            lastIndex = -1;
        }
    }
}
