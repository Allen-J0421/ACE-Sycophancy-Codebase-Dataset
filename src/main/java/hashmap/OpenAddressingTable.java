package hashmap;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Package-private open-addressing, linear-probing storage core shared by
 * {@link OpenAddressingHashMap} and {@link OpenAddressingHashSet}.
 *
 * <p>This class owns everything the two public collections have in common: the
 * slot storage, hashing, linear probing, tombstone deletion, automatic resizing,
 * and fail-fast iteration. The collections are thin adapters that compose a table
 * and expose it as a {@code Map} or a {@code Set} &mdash; composition is used
 * rather than a shared superclass because both already extend
 * {@code AbstractMap}/{@code AbstractSet} and Java allows only single inheritance.
 *
 * <p>A table optionally maintains a parallel {@code values} array, selected by the
 * {@code tracksValues} constructor flag. A map sets it; a set leaves it off, so a
 * set carries <em>no</em> per-element value storage at all. Slot state lives in the
 * {@code keys} array: {@code null} marks an unused slot, {@link #TOMBSTONE} marks a
 * deleted slot, and any other reference marks a live entry &mdash; so occupancy
 * depends only on the key and a live entry may hold a {@code null} value.
 *
 * <p>The table length is always a power of two; the slot index is therefore a
 * bit-mask rather than a modulo, and the table grows once the fraction of occupied
 * slots (live entries plus tombstones) exceeds the configured load factor.
 *
 * <p>Not thread-safe.
 */
final class OpenAddressingTable {

    /** Default initial capacity; must be a power of two. */
    static final int DEFAULT_CAPACITY = 16;

    /** Default fraction of occupied slots that triggers a resize. */
    static final double DEFAULT_LOAD_FACTOR = 0.5;

    /** Sentinel stored in {@code keys} to mark a deleted slot, distinct from {@code null}. */
    private static final Object TOMBSTONE = new Object();

    private Object[] keys;
    private Object[] values;             // null when !tracksValues
    private final boolean tracksValues;

    private int size;                    // live entries
    private int occupiedSlots;           // live entries + tombstones
    private int modCount;
    private final double loadFactor;

    OpenAddressingTable(int initialCapacity, double loadFactor, boolean tracksValues) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be positive: " + initialCapacity);
        }
        if (!(loadFactor > 0.0 && loadFactor < 1.0) || Double.isNaN(loadFactor)) {
            throw new IllegalArgumentException("loadFactor must be in (0, 1): " + loadFactor);
        }
        int capacity = tableSizeFor(initialCapacity);
        this.keys = new Object[capacity];
        this.tracksValues = tracksValues;
        this.values = tracksValues ? new Object[capacity] : null;
        this.loadFactor = loadFactor;
    }

    // ----------------------------------------------------------------------
    // Size / state
    // ----------------------------------------------------------------------

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    int capacity() {
        return keys.length;
    }

    // ----------------------------------------------------------------------
    // Slot access (by index)
    // ----------------------------------------------------------------------

    Object keyAt(int index) {
        return keys[index];
    }

    Object valueAt(int index) {
        return values[index];
    }

    Object setValueAt(int index, Object value) {
        Object previous = values[index];
        values[index] = value;
        return previous;
    }

    // ----------------------------------------------------------------------
    // Core operations
    // ----------------------------------------------------------------------

    /** Returns the index of the live slot for {@code key}, or {@code -1} if absent. */
    int indexOf(Object key) {
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

    /**
     * Ensures {@code key} occupies a slot, growing the table first if needed.
     *
     * @return the slot index if the key was already present; the bitwise complement
     *         ({@code ~index}, a negative number) of the slot index if it was newly
     *         inserted. {@link OpenAddressingHashMap} uses the sign to decide whether
     *         {@code put} is replacing or adding.
     * @throws NullPointerException if {@code key} is {@code null}
     */
    int insert(Object key) {
        Objects.requireNonNull(key, "key/element must not be null");

        if (occupiedSlots + 1 > keys.length * loadFactor) {
            rehash(keys.length << 1);
        }

        int firstTombstone = -1;
        int index = indexFor(key, keys.length);
        while (true) {
            Object k = keys[index];
            if (k == null) {
                int target = (firstTombstone >= 0) ? firstTombstone : index;
                if (firstTombstone < 0) {
                    occupiedSlots++;
                }
                keys[target] = key;
                size++;
                modCount++;
                return ~target;
            }
            if (k == TOMBSTONE) {
                if (firstTombstone < 0) {
                    firstTombstone = index;
                }
            } else if (k.equals(key)) {
                return index;
            }
            index = next(index);
        }
    }

    /** Tombstones the live slot at {@code index}, releasing any value for GC. */
    void deleteAt(int index) {
        keys[index] = TOMBSTONE;
        if (tracksValues) {
            values[index] = null;
        }
        size--;
        modCount++;
        // occupiedSlots is unchanged: the tombstone still occupies the slot.
    }

    void clear() {
        keys = new Object[DEFAULT_CAPACITY];
        values = tracksValues ? new Object[DEFAULT_CAPACITY] : null;
        size = 0;
        occupiedSlots = 0;
        modCount++;
    }

    /** Linear scan for a value; only meaningful for value-tracking (map) tables. */
    boolean containsValue(Object value) {
        for (int i = 0; i < keys.length; i++) {
            if (isLive(i) && Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }

    // ----------------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------------

    private boolean isLive(int i) {
        Object k = keys[i];
        return k != null && k != TOMBSTONE;
    }

    private static int indexFor(Object key, int length) {
        int h = key.hashCode();
        h ^= (h >>> 16); // spread high bits into low bits to reduce clustering
        return h & (length - 1);
    }

    private int next(int index) {
        return (index + 1) & (keys.length - 1);
    }

    /** Rebuilds both arrays at {@code newCapacity}, dropping tombstones. */
    private void rehash(int newCapacity) {
        Object[] oldKeys = keys;
        Object[] oldValues = values;
        keys = new Object[newCapacity];
        values = tracksValues ? new Object[newCapacity] : null;
        occupiedSlots = 0;
        // size is unchanged: every live slot is re-placed exactly once.
        for (int i = 0; i < oldKeys.length; i++) {
            Object k = oldKeys[i];
            if (k != null && k != TOMBSTONE) {
                int j = freeSlot(k);
                keys[j] = k;
                if (tracksValues) {
                    values[j] = oldValues[i];
                }
                occupiedSlots++;
            }
        }
    }

    /** First {@code null} slot on {@code key}'s probe path; used during rehash. */
    private int freeSlot(Object key) {
        int index = indexFor(key, keys.length);
        while (keys[index] != null) {
            index = next(index);
        }
        return index;
    }

    private static int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : n + 1;
    }

    // ----------------------------------------------------------------------
    // Fail-fast iteration over live slot indices
    // ----------------------------------------------------------------------

    Cursor cursor() {
        return new Cursor();
    }

    /**
     * Walks the live slots in unspecified order, exposing each by index. Shared by
     * the map's entry-set iterator and the set's element iterator; supports removal.
     */
    final class Cursor {
        private int expectedModCount = modCount;
        private int scan;            // next table index to examine
        private int nextIndex = -1;  // index of the next live slot, or -1
        private int lastIndex = -1;  // index of the last returned slot, for remove()

        private Cursor() {
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

        boolean hasNext() {
            return nextIndex >= 0;
        }

        int nextIndex() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (nextIndex < 0) {
                throw new NoSuchElementException();
            }
            lastIndex = nextIndex;
            advance();
            return lastIndex;
        }

        void remove() {
            if (lastIndex < 0) {
                throw new IllegalStateException("next() has not been called, or remove() already called");
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            deleteAt(lastIndex);
            expectedModCount = modCount; // stay consistent so iteration may continue
            lastIndex = -1;
        }
    }
}
