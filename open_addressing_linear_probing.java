final class OpenAddressingIntMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final double MAX_LOAD_FACTOR = 0.6;
    private static final double MIN_LOAD_FACTOR = 0.2;
    private static final double MAX_TOMBSTONE_RATIO = 0.2;
    private static final int NOT_FOUND = -1;

    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte DELETED = 2;

    private final int minimumCapacity;
    private int[] keys;
    private int[] values;
    private byte[] states;
    private int size;
    private int tombstones;

    private static final class ProbeResult {
        private final int foundIndex;
        private final int insertionIndex;

        private ProbeResult(int foundIndex, int insertionIndex) {
            this.foundIndex = foundIndex;
            this.insertionIndex = insertionIndex;
        }

        private boolean found() {
            return foundIndex != NOT_FOUND;
        }
    }

    OpenAddressingIntMap() {
        this(DEFAULT_CAPACITY);
    }

    OpenAddressingIntMap(int initialCapacity) {
        minimumCapacity = normalizeCapacity(initialCapacity);
        allocateArrays(minimumCapacity);
    }

    void put(int key, int value) {
        ensureCapacityForInsert();

        ProbeResult probe = probe(key);
        if (!probe.found() && probe.insertionIndex == NOT_FOUND) {
            resize(Math.max(capacity() * 2, recommendedCapacityFor(size + 1)));
            probe = probe(key);
        }

        writeEntry(probe, key, value);
    }

    int getOrDefault(int key, int defaultValue) {
        int index = findIndex(key);
        return index != NOT_FOUND ? values[index] : defaultValue;
    }

    int removeOrDefault(int key, int defaultValue) {
        int index = findIndex(key);
        return index != NOT_FOUND ? removeAt(index) : defaultValue;
    }

    boolean containsKey(int key) {
        return findIndex(key) != NOT_FOUND;
    }

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void clear() {
        allocateArrays(minimumCapacity);
        size = 0;
        tombstones = 0;
    }

    void display() {
        forEachEntry(new EntryVisitor() {
            @Override
            public void accept(int key, int value) {
                System.out.println(key + " " + value);
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean[] first = {true};

        forEachEntry(new EntryVisitor() {
            @Override
            public void accept(int key, int value) {
                if (!first[0]) {
                    builder.append(", ");
                }
                builder.append(key).append('=').append(value);
                first[0] = false;
            }
        });

        return builder.append('}').toString();
    }

    int hashIndex(int key) {
        return Math.floorMod(key, capacity());
    }

    int findIndex(int key) {
        return probe(key).foundIndex;
    }

    int valueAt(int index) {
        return values[index];
    }

    int removeAt(int index) {
        int removedValue = values[index];
        states[index] = DELETED;
        size--;
        tombstones++;
        rebalanceAfterRemoval();
        return removedValue;
    }

    private void ensureCapacityForInsert() {
        if (needsRebuildBeforeInsert()) {
            resize(recommendedCapacityFor(size + 1));
        }
    }

    private boolean needsRebuildBeforeInsert() {
        return exceedsActiveLoad(size + 1)
                || exceedsOccupiedLoad(size + tombstones + 1)
                || exceedsTombstoneLoad();
    }

    private void rebalanceAfterRemoval() {
        if (size == 0) {
            resize(minimumCapacity);
            return;
        }

        if (exceedsTombstoneLoad() || belowMinLoad()) {
            resize(recommendedCapacityFor(size));
        }
    }

    private ProbeResult probe(int key) {
        int index = hashIndex(key);
        int firstDeletedIndex = NOT_FOUND;

        for (int probes = 0; probes < capacity(); probes++) {
            byte state = states[index];

            if (state == EMPTY) {
                int insertionIndex = firstDeletedIndex != NOT_FOUND ? firstDeletedIndex : index;
                return new ProbeResult(NOT_FOUND, insertionIndex);
            }

            if (state == DELETED) {
                if (firstDeletedIndex == NOT_FOUND) {
                    firstDeletedIndex = index;
                }
            } else if (keys[index] == key) {
                return new ProbeResult(index, index);
            }

            index = nextIndex(index);
        }

        return new ProbeResult(NOT_FOUND, firstDeletedIndex);
    }

    private void writeEntry(ProbeResult probe, int key, int value) {
        int targetIndex = probe.found() ? probe.foundIndex : probe.insertionIndex;

        if (!probe.found()) {
            if (states[targetIndex] == EMPTY) {
                size++;
            } else if (states[targetIndex] == DELETED) {
                size++;
                tombstones--;
            }
        }

        keys[targetIndex] = key;
        values[targetIndex] = value;
        states[targetIndex] = OCCUPIED;
    }

    private void resize(int newCapacity) {
        int[] oldKeys = keys;
        int[] oldValues = values;
        byte[] oldStates = states;

        allocateArrays(normalizeCapacity(newCapacity));
        size = 0;
        tombstones = 0;

        for (int index = 0; index < oldStates.length; index++) {
            if (oldStates[index] == OCCUPIED) {
                reinsert(oldKeys[index], oldValues[index]);
            }
        }
    }

    private void reinsert(int key, int value) {
        ProbeResult probe = probe(key);
        int targetIndex = probe.insertionIndex;

        keys[targetIndex] = key;
        values[targetIndex] = value;
        states[targetIndex] = OCCUPIED;
        size++;
    }

    private void forEachEntry(EntryVisitor visitor) {
        for (int index = 0; index < capacity(); index++) {
            if (states[index] == OCCUPIED) {
                visitor.accept(keys[index], values[index]);
            }
        }
    }

    private void allocateArrays(int capacity) {
        keys = new int[capacity];
        values = new int[capacity];
        states = new byte[capacity];
    }

    private boolean exceedsActiveLoad(int targetSize) {
        return (double) targetSize / capacity() > MAX_LOAD_FACTOR;
    }

    private boolean exceedsOccupiedLoad(int occupiedSlots) {
        return (double) occupiedSlots / capacity() > MAX_LOAD_FACTOR;
    }

    private boolean exceedsTombstoneLoad() {
        return tombstones > 0 && (double) tombstones / capacity() > MAX_TOMBSTONE_RATIO;
    }

    private boolean belowMinLoad() {
        return capacity() > minimumCapacity && (double) size / capacity() < MIN_LOAD_FACTOR;
    }

    private int recommendedCapacityFor(int targetSize) {
        int recommendedCapacity = minimumCapacity;

        while ((double) targetSize / recommendedCapacity > MAX_LOAD_FACTOR) {
            recommendedCapacity *= 2;
        }

        return recommendedCapacity;
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity();
    }

    private int capacity() {
        return states.length;
    }

    private int normalizeCapacity(int requestedCapacity) {
        return Math.max(1, requestedCapacity);
    }

    private interface EntryVisitor {
        void accept(int key, int value);
    }
}

final class hashMap {
    private static final int NOT_FOUND = -1;

    private final OpenAddressingIntMap delegate;

    hashMap() {
        this.delegate = new OpenAddressingIntMap();
    }

    hashMap(int initialCapacity) {
        this.delegate = new OpenAddressingIntMap(initialCapacity);
    }

    int hashCode(int key) {
        return delegate.hashIndex(key);
    }

    Integer putValue(int key, int value) {
        int index = delegate.findIndex(key);
        Integer previousValue = index != NOT_FOUND ? delegate.valueAt(index) : null;
        delegate.put(key, value);
        return previousValue;
    }

    void put(int key, int value) {
        delegate.put(key, value);
    }

    void insertNode(int key, int value) {
        delegate.put(key, value);
    }

    Integer getValue(int key) {
        int index = delegate.findIndex(key);
        return index != NOT_FOUND ? delegate.valueAt(index) : null;
    }

    int get(int key) {
        return delegate.getOrDefault(key, NOT_FOUND);
    }

    Integer removeValue(int key) {
        int index = delegate.findIndex(key);
        return index != NOT_FOUND ? delegate.removeAt(index) : null;
    }

    int remove(int key) {
        return delegate.removeOrDefault(key, NOT_FOUND);
    }

    int deleteNode(int key) {
        return remove(key);
    }

    boolean containsKey(int key) {
        return delegate.containsKey(key);
    }

    int size() {
        return delegate.size();
    }

    int sizeofMap() {
        return delegate.size();
    }

    boolean isEmpty() {
        return delegate.isEmpty();
    }

    void clear() {
        delegate.clear();
    }

    void display() {
        delegate.display();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    public static void main(String[] args) {
        hashMap map = new hashMap();
        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3);
        map.display();
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));
    }
}
