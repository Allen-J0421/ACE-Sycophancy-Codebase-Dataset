import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private static final int NOT_FOUND = -1;

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return NOT_FOUND;
        }

        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node existingNode = cache.get(key);
        if (existingNode != null) {
            existingNode.value = value;
            moveToFront(existingNode);
            return;
        }

        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        insertAfterHead(newNode);

        if (cache.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    public int size() {
        return cache.size();
    }

    private void moveToFront(Node node) {
        unlink(node);
        insertAfterHead(node);
    }

    private void evictLeastRecentlyUsed() {
        Node leastRecentlyUsed = tail.prev;
        if (leastRecentlyUsed == head) {
            return;
        }

        unlink(leastRecentlyUsed);
        cache.remove(leastRecentlyUsed.key);
    }

    private void insertAfterHead(Node node) {
        Node firstEntry = head.next;
        head.next = node;
        node.prev = head;
        node.next = firstEntry;
        firstEntry.prev = node;
    }

    private void unlink(Node node) {
        Node previous = node.prev;
        Node next = node.next;
        previous.next = next;
        next.prev = previous;
        node.prev = null;
        node.next = null;
    }

    private static final class Node {
        private final int key;
        private int value;
        private Node next;
        private Node prev;

        private Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
