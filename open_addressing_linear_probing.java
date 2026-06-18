final class hashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final double MAX_LOAD_FACTOR = 0.6;
    private static final double MAX_TOMBSTONE_RATIO = 0.2;
    private static final int NOT_FOUND = -1;
    private static final Entry TOMBSTONE = Entry.tombstone();

    private Entry[] entries;
    private int size;
    private int tombstones;

    private static final class Entry {
        private final int key;
        private final int value;
        private final boolean deleted;

        private Entry(int key, int value, boolean deleted) {
            this.key = key;
            this.value = value;
            this.deleted = deleted;
        }

        private static Entry active(int key, int value) {
            return new Entry(key, value, false);
        }

        private static Entry tombstone() {
            return new Entry(0, 0, true);
        }
    }

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

    hashMap() {
        this(DEFAULT_CAPACITY);
    }

    hashMap(int initialCapacity) {
        entries = new Entry[normalizeCapacity(initialCapacity)];
    }

    int hashCode(int key) {
        return Math.floorMod(key, capacity());
    }

    void put(int key, int value) {
        ensureInsertCapacity();

        ProbeResult probe = probe(key);
        if (!probe.found() && probe.insertionIndex == NOT_FOUND) {
            resize(capacity() * 2);
            probe = probe(key);
        }

        writeEntry(probe, key, value);
    }

    void insertNode(int key, int value) {
        put(key, value);
    }

    int remove(int key) {
        ProbeResult probe = probe(key);
        if (!probe.found()) {
            return NOT_FOUND;
        }

        int value = entries[probe.foundIndex].value;
        entries[probe.foundIndex] = TOMBSTONE;
        size--;
        tombstones++;
        compactIfNeeded();
        return value;
    }

    int deleteNode(int key) {
        return remove(key);
    }

    int get(int key) {
        ProbeResult probe = probe(key);
        return probe.found() ? entries[probe.foundIndex].value : NOT_FOUND;
    }

    boolean containsKey(int key) {
        return probe(key).found();
    }

    int size() {
        return size;
    }

    int sizeofMap() {
        return size();
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        for (Entry entry : entries) {
            if (isActive(entry)) {
                System.out.println(entry.key + " " + entry.value);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (Entry entry : entries) {
            if (!isActive(entry)) {
                continue;
            }
            if (!first) {
                builder.append(", ");
            }
            builder.append(entry.key).append('=').append(entry.value);
            first = false;
        }

        return builder.append('}').toString();
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
        if (size == 0) {
            resize(capacity());
            return;
        }

        if (exceedsTombstoneLoad()) {
            resize(capacity());
        }
    }

    private ProbeResult probe(int key) {
        int index = hashCode(key);
        int firstTombstone = NOT_FOUND;

        for (int probes = 0; probes < capacity(); probes++) {
            Entry entry = entries[index];

            if (entry == null) {
                int insertionIndex = firstTombstone != NOT_FOUND ? firstTombstone : index;
                return new ProbeResult(NOT_FOUND, insertionIndex);
            }

            if (entry.deleted) {
                if (firstTombstone == NOT_FOUND) {
                    firstTombstone = index;
                }
            } else if (entry.key == key) {
                return new ProbeResult(index, index);
            }

            index = nextIndex(index);
        }

        return new ProbeResult(NOT_FOUND, firstTombstone);
    }

    private void writeEntry(ProbeResult probe, int key, int value) {
        int targetIndex = probe.found() ? probe.foundIndex : probe.insertionIndex;
        Entry previousEntry = entries[targetIndex];

        if (!probe.found()) {
            if (previousEntry == null) {
                size++;
            } else if (previousEntry.deleted) {
                size++;
                tombstones--;
            }
        }

        entries[targetIndex] = Entry.active(key, value);
    }

    private void resize(int newCapacity) {
        Entry[] oldEntries = entries;
        entries = new Entry[normalizeCapacity(newCapacity)];
        size = 0;
        tombstones = 0;

        for (Entry entry : oldEntries) {
            if (isActive(entry)) {
                reinsert(entry);
            }
        }
    }

    private void reinsert(Entry entry) {
        ProbeResult probe = probe(entry.key);
        entries[probe.insertionIndex] = Entry.active(entry.key, entry.value);
        size++;
    }

    private boolean isActive(Entry entry) {
        return entry != null && !entry.deleted;
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
        return entries.length;
    }

    private int normalizeCapacity(int requestedCapacity) {
        return Math.max(1, requestedCapacity);
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
