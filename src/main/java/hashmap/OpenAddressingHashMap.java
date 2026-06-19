package hashmap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Map} implemented with <em>open addressing</em>, <em>linear probing</em>,
 * and a <em>struct-of-arrays</em> storage layout.
 *
 * <p>The storage mechanics &mdash; parallel {@code keys}/{@code values} arrays,
 * hashing, probing, tombstone deletion, and resizing &mdash; live in the shared
 * package-private {@link OpenAddressingTable}, which this map composes and the
 * sibling {@link OpenAddressingHashSet} reuses. This class is a thin adapter that
 * presents the table as a {@code java.util.Map}.
 *
 * <p>Keys and values are generic. {@code null} keys are not permitted; {@code null}
 * values are. The table length is always a power of two and grows automatically
 * once the load factor is exceeded, keeping {@code get}/{@code put}/{@code remove}
 * amortized {@code O(1)}.
 *
 * <p>This class extends {@link AbstractMap} so that it inherits the standard
 * {@code Map} contracts for {@link #equals}, {@link #hashCode}, {@link #toString},
 * {@link #putAll}, {@link #keySet}, and {@link #values}, while overriding the
 * hot-path operations with constant-time implementations. The {@link #entrySet()}
 * view, its iterator, and the derived key/value views are <em>fail-fast</em>:
 * structurally modifying the map during iteration (other than through the
 * iterator's own {@code remove}) throws
 * {@link java.util.ConcurrentModificationException}.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public final class OpenAddressingHashMap<K, V> extends AbstractMap<K, V> {

    private final OpenAddressingTable table;

    /** Creates an empty map with the default capacity and load factor. */
    public OpenAddressingHashMap() {
        this(OpenAddressingTable.DEFAULT_CAPACITY, OpenAddressingTable.DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates an empty map with the given initial capacity and the default load factor.
     *
     * @param initialCapacity a hint for the number of entries to hold without resizing
     * @throws IllegalArgumentException if {@code initialCapacity} is not positive
     */
    public OpenAddressingHashMap(int initialCapacity) {
        this(initialCapacity, OpenAddressingTable.DEFAULT_LOAD_FACTOR);
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
        this.table = new OpenAddressingTable(initialCapacity, loadFactor, true);
    }

    /**
     * Creates a map containing the mappings of {@code source}, sized to hold them
     * without resizing.
     *
     * @param source the mappings to copy
     * @throws NullPointerException if {@code source} is {@code null}, or contains a null key
     */
    public OpenAddressingHashMap(Map<? extends K, ? extends V> source) {
        this(Math.max(OpenAddressingTable.DEFAULT_CAPACITY,
            (int) (source.size() / OpenAddressingTable.DEFAULT_LOAD_FACTOR) + 1));
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
        int index = table.insert(key);
        if (index >= 0) { // key already present
            return (V) table.setValueAt(index, value);
        }
        table.setValueAt(~index, value); // newly inserted slot
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int index = table.indexOf(key);
        return index < 0 ? null : (V) table.valueAt(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getOrDefault(Object key, V defaultValue) {
        int index = table.indexOf(key);
        return index < 0 ? defaultValue : (V) table.valueAt(index);
    }

    @Override
    public boolean containsKey(Object key) {
        return table.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        return table.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = table.indexOf(key);
        if (index < 0) {
            return null;
        }
        V previous = (V) table.valueAt(index);
        table.deleteAt(index);
        return previous;
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

    /**
     * Returns a live, fail-fast {@link Set} view of the mappings. The view supports
     * element removal (which removes the mapping from the map) but not addition.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    // ----------------------------------------------------------------------
    // Views
    // ----------------------------------------------------------------------

    /**
     * A {@link Map.Entry} backed by a table slot index. {@code getValue}/
     * {@code setValue} read and write the table directly, so the entry stays in sync
     * with the map (writes are visible to the map, per the {@code Map.Entry}
     * contract). The key is captured at construction, since a live slot's key never
     * changes while the slot remains live.
     */
    private final class IndexEntry implements Map.Entry<K, V> {
        private final int index;
        private final K key;

        @SuppressWarnings("unchecked")
        IndexEntry(int index) {
            this.index = index;
            this.key = (K) table.keyAt(index);
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) table.valueAt(index);
        }

        @Override
        @SuppressWarnings("unchecked")
        public V setValue(V newValue) {
            return (V) table.setValueAt(index, newValue);
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
            return table.size();
        }

        @Override
        public void clear() {
            table.clear();
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
            int index = table.indexOf(e.getKey());
            return index >= 0 && Objects.equals(table.valueAt(index), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            }
            int index = table.indexOf(e.getKey());
            if (index >= 0 && Objects.equals(table.valueAt(index), e.getValue())) {
                table.deleteAt(index);
                return true;
            }
            return false;
        }
    }

    /** Fail-fast iterator over entries, delegating slot walking to the table cursor. */
    private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private final OpenAddressingTable.Cursor cursor = table.cursor();

        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            return new IndexEntry(cursor.nextIndex());
        }

        @Override
        public void remove() {
            cursor.remove();
        }
    }
}
