final class OpenAddressingIntMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final double MAX_LOAD_FACTOR = 0.6;
    private static final double MAX_TOMBSTONE_RATIO = 0.2;

    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte DELETED = 2;

    private int[] keys;
    private int[] values;
    private byte[] states;
    private int size;
    private int tombstones;

    private static final class ProbeResult {
        private static final int NOT_FOUND = -1;

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
        allocateArrays(normalizeCapacity(initialCapacity));
    }

    Integer putValue(int key, int value) {
        ensureInsertCapacity();

        ProbeResult probe = probe(key);
        if (!probe.found() && probe.insertionIndex == ProbeResult.NOT_FOUND) {
            resize(capacity() * 2);
            probe = probe(key);
        }

        return writeEntry(probe, key, value);
    }

    void put(int key, int value) {
        putValue(key, value);
    }

    Integer getValue(int key) {
        ProbeResult probe = probe(key);
        return probe.found() ? values[probe.foundIndex] : null;
    }

    int getOrDefault(int key, int defaultValue) {
        Integer value = getValue(key);
        return value != null ? value : defaultValue;
    }

    Integer removeValue(int key) {
        ProbeResult probe = probe(key);
        if (!probe.found()) {
            return null;
        }

        int index = probe.foundIndex;
        int removedValue = values[index];

        states[index] = DELETED;
        size--;
        tombstones++;
        compactIfNeeded();
        return removedValue;
    }

    boolean containsKey(int key) {
        return probe(key).found();
    }

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void clear() {
        allocateArrays(capacity());
        size = 0;
        tombstones = 0;
    }

    void display() {
        for (int index = 0; index < capacity(); index++) {
            if (states[index] == OCCUPIED) {
                System.out.println(keys[index] + " " + values[index]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (int index = 0; index < capacity(); index++) {
            if (states[index] != OCCUPIED) {
                continue;
            }
            if (!first) {
                builder.append(", ");
            }
            builder.append(keys[index]).append('=').append(values[index]);
            first = false;
        }

        return builder.append('}').toString();
    }

    int hashIndex(int key) {
        return Math.floorMod(key, capacity());
    }

    private void ensureInsertCapacity() {
        if (exceedsActiveLoad(size + 1)) {
            resize(capacity() * 2);
            return;
        }

        if (exceedsOccupiedLoad(size + tombstones + 1) || exceedsTombstoneLoad()) {
            resize(capacity());
        }
    }

    private void compactIfNeeded() {
        if (size == 0 || exceedsTombstoneLoad()) {
            resize(capacity());
        }
    }

    private ProbeResult probe(int key) {
        int index = hashIndex(key);
        int firstDeletedIndex = ProbeResult.NOT_FOUND;

        for (int probes = 0; probes < capacity(); probes++) {
            byte state = states[index];

            if (state == EMPTY) {
                int insertionIndex =
                        firstDeletedIndex != ProbeResult.NOT_FOUND ? firstDeletedIndex : index;
                return new ProbeResult(ProbeResult.NOT_FOUND, insertionIndex);
            }

            if (state == DELETED) {
                if (firstDeletedIndex == ProbeResult.NOT_FOUND) {
                    firstDeletedIndex = index;
                }
            } else if (keys[index] == key) {
                return new ProbeResult(index, index);
            }

            index = nextIndex(index);
        }

        return new ProbeResult(ProbeResult.NOT_FOUND, firstDeletedIndex);
    }

    private Integer writeEntry(ProbeResult probe, int key, int value) {
        int targetIndex = probe.found() ? probe.foundIndex : probe.insertionIndex;
        Integer previousValue = probe.found() ? values[targetIndex] : null;

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
        return previousValue;
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

    private int nextIndex(int index) {
        return (index + 1) % capacity();
    }

    private int capacity() {
        return states.length;
    }

    private int normalizeCapacity(int requestedCapacity) {
        return Math.max(1, requestedCapacity);
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
        return delegate.putValue(key, value);
    }

    void put(int key, int value) {
        delegate.put(key, value);
    }

    void insertNode(int key, int value) {
        delegate.put(key, value);
    }

    Integer getValue(int key) {
        return delegate.getValue(key);
    }

    int get(int key) {
        return delegate.getOrDefault(key, NOT_FOUND);
    }

    Integer removeValue(int key) {
        return delegate.removeValue(key);
    }

    int remove(int key) {
        Integer removedValue = delegate.removeValue(key);
        return removedValue != null ? removedValue : NOT_FOUND;
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
