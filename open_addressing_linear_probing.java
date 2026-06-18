class OpenAddressingIntMap {
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

    static final class ValueResult {
        private static final ValueResult MISSING = new ValueResult(false, 0);

        private final boolean present;
        private final int value;

        private ValueResult(boolean present, int value) {
            this.present = present;
            this.value = value;
        }

        private static ValueResult present(int value) {
            return new ValueResult(true, value);
        }

        private static ValueResult missing() {
            return MISSING;
        }

        boolean isPresent() {
            return present;
        }

        int value() {
            return value;
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
        putResult(key, value);
    }

    ValueResult putResult(int key, int value) {
        ProbeResult probe = probe(key);
        if (!probe.found()) {
            probe = prepareInsertionSlot(key, probe);
        }

        return writeEntry(probe, key, value);
    }

    int getOrDefault(int key, int defaultValue) {
        ValueResult result = getResult(key);
        return result.isPresent() ? result.value() : defaultValue;
    }

    ValueResult getResult(int key) {
        ProbeResult probe = probe(key);
        return probe.found() ? ValueResult.present(values[probe.foundIndex]) : ValueResult.missing();
    }

    int removeOrDefault(int key, int defaultValue) {
        ValueResult result = removeResult(key);
        return result.isPresent() ? result.value() : defaultValue;
    }

    ValueResult removeResult(int key) {
        ProbeResult probe = probe(key);
        return probe.found()
                ? ValueResult.present(removeAt(probe.foundIndex))
                : ValueResult.missing();
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
        allocateArrays(minimumCapacity);
        size = 0;
        tombstones = 0;
    }

    void display() {
        for (int index = 0; index < capacity(); index++) {
            if (isOccupied(index)) {
                System.out.println(keys[index] + " " + values[index]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (int index = 0; index < capacity(); index++) {
            if (!isOccupied(index)) {
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

    private ProbeResult prepareInsertionSlot(int key, ProbeResult probe) {
        if (!needsRebuildBeforeInsert(probe)) {
            return probe;
        }

        resize(recommendedCapacityFor(size + 1));
        return probe(key);
    }

    private boolean needsRebuildBeforeInsert(ProbeResult probe) {
        if (probe.insertionIndex == NOT_FOUND) {
            return true;
        }

        return exceedsActiveLoad(size + 1)
                || exceedsOccupiedLoad(size + tombstones + 1)
                || exceedsTombstoneLoad();
    }

    private int removeAt(int index) {
        int removedValue = values[index];
        states[index] = DELETED;
        size--;
        tombstones++;
        rebalanceAfterRemoval();
        return removedValue;
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

    private ValueResult writeEntry(ProbeResult probe, int key, int value) {
        int targetIndex = probe.found() ? probe.foundIndex : probe.insertionIndex;
        ValueResult previousResult = probe.found()
                ? ValueResult.present(values[targetIndex])
                : ValueResult.missing();

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
        return previousResult;
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

    private boolean isOccupied(int index) {
        return states[index] == OCCUPIED;
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
}

final class hashMap extends OpenAddressingIntMap {
    private static final int NOT_FOUND = -1;

    hashMap() {
        super();
    }

    hashMap(int initialCapacity) {
        super(initialCapacity);
    }

    int hashCode(int key) {
        return hashIndex(key);
    }

    Integer putValue(int key, int value) {
        OpenAddressingIntMap.ValueResult result = putResult(key, value);
        return result.isPresent() ? result.value() : null;
    }

    void put(int key, int value) {
        super.put(key, value);
    }

    void insertNode(int key, int value) {
        super.put(key, value);
    }

    Integer getValue(int key) {
        OpenAddressingIntMap.ValueResult result = getResult(key);
        return result.isPresent() ? result.value() : null;
    }

    int get(int key) {
        return getOrDefault(key, NOT_FOUND);
    }

    Integer removeValue(int key) {
        OpenAddressingIntMap.ValueResult result = removeResult(key);
        return result.isPresent() ? result.value() : null;
    }

    int remove(int key) {
        return removeOrDefault(key, NOT_FOUND);
    }

    int deleteNode(int key) {
        return remove(key);
    }

    int sizeofMap() {
        return size();
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
