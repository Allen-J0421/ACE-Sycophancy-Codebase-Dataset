import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class LRUCache<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head; // sentinel: MRU side
    private final Node<K, V> tail; // sentinel: LRU side

    LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    Optional<V> get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return Optional.empty();
        }
        moveToFront(node);
        return Optional.of(node.value);
    }

    void put(K key, V value) {
        Node<K, V> existing = cache.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        Node<K, V> node = new Node<>(key, value);
        cache.put(key, node);
        insertAtFront(node);

        if (cache.size() > capacity) {
            Node<K, V> lru = tail.prev;
            unlink(lru);
            cache.remove(lru.key);
        }
    }

    int size() {
        return cache.size();
    }

    private void moveToFront(Node<K, V> node) {
        unlink(node);
        insertAtFront(node);
    }

    private void insertAtFront(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

class Main {
    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // Optional[1]
        cache.put(3, 3);
        System.out.println(cache.get(2)); // Optional.empty
        cache.put(4, 4);
        System.out.println(cache.get(1)); // Optional.empty
        System.out.println(cache.get(3)); // Optional[3]
        System.out.println(cache.get(4)); // Optional[4]
    }
}
