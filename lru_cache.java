import java.util.HashMap;
import java.util.Map;

/**
 * A fixed-capacity key/value store with a defined eviction policy.
 */
interface Cache<K, V> {

    /** Returns the value mapped to {@code key}, or {@code null} if absent. */
    V get(K key);

    /** Returns the value mapped to {@code key}, or {@code defaultValue} if absent. */
    V getOrDefault(K key, V defaultValue);

    /** Inserts or updates the mapping for {@code key}. */
    void put(K key, V value);

    /** Returns {@code true} if a mapping for {@code key} exists. */
    boolean containsKey(K key);

    /** Returns the number of entries currently held. */
    int size();

    /** Returns the maximum number of entries this cache may hold. */
    int capacity();
}

/**
 * A {@link Cache} that evicts the least-recently-used entry once capacity is
 * exceeded.
 *
 * <p>Both {@link #get} and {@link #put} run in O(1): a {@link HashMap} provides
 * constant-time lookup while a doubly linked list, ordered most- to
 * least-recently-used, provides constant-time reordering and eviction. The list
 * uses sentinel head and tail nodes so insertion and removal never need to test
 * for {@code null} neighbours.
 *
 * <p>This implementation is not thread-safe.
 */
class LRUCache<K, V> implements Cache<K, V> {

    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> entries;
    private final Node<K, V> head; // sentinel: head.next is most-recently-used
    private final Node<K, V> tail; // sentinel: tail.prev is least-recently-used

    LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        this.entries = new HashMap<>();
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public V get(K key) {
        return getOrDefault(key, null);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        Node<K, V> node = entries.get(key);
        if (node == null) {
            return defaultValue;
        }
        moveToFront(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        Node<K, V> existing = entries.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        Node<K, V> node = new Node<>(key, value);
        entries.put(key, node);
        addToFront(node);

        if (entries.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    @Override
    public boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    private void evictLeastRecentlyUsed() {
        Node<K, V> lru = tail.prev;
        unlink(lru);
        entries.remove(lru.key);
    }

    /** Moves an already-linked node to the most-recently-used position. */
    private void moveToFront(Node<K, V> node) {
        unlink(node);
        addToFront(node);
    }

    /** Links a detached node directly after the head sentinel. */
    private void addToFront(Node<K, V> node) {
        Node<K, V> first = head.next;
        node.prev = head;
        node.next = first;
        head.next = node;
        first.prev = node;
    }

    /** Detaches a node from the list, joining its neighbours. */
    private void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }
}

public class Main {
    public static void main(String[] args) {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.getOrDefault(1, -1)); // 1
        cache.put(3, 3);                                // evicts key 2
        System.out.println(cache.getOrDefault(2, -1)); // -1 (evicted)
        cache.put(4, 4);                                // evicts key 1
        System.out.println(cache.getOrDefault(1, -1)); // -1 (evicted)
        System.out.println(cache.getOrDefault(3, -1)); // 3
        System.out.println(cache.getOrDefault(4, -1)); // 4
    }
}
