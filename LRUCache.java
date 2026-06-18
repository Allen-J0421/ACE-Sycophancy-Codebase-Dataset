import java.util.HashMap;
import java.util.Map;

/**
 * A least-recently-used cache for integer keys and values.
 */
public final class LRUCache {
    private static final class Node {
        final int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> entries;
    private final Node sentinelHead;
    private final Node sentinelTail;

    public LRUCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        this.capacity = capacity;
        this.entries = new HashMap<>();
        this.sentinelHead = new Node(-1, -1);
        this.sentinelTail = new Node(-1, -1);
        sentinelHead.next = sentinelTail;
        sentinelTail.prev = sentinelHead;
    }

    public int get(int key) {
        Node node = entries.get(key);
        if (node == null) {
            return -1;
        }

        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        Node existing = entries.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        Node node = new Node(key, value);
        entries.put(key, node);
        insertAtFront(node);

        if (entries.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    public int size() {
        return entries.size();
    }

    private void moveToFront(Node node) {
        detach(node);
        insertAtFront(node);
    }

    private void insertAtFront(Node node) {
        Node firstRealNode = sentinelHead.next;
        sentinelHead.next = node;
        node.prev = sentinelHead;
        node.next = firstRealNode;
        firstRealNode.prev = node;
    }

    private void detach(Node node) {
        Node previous = node.prev;
        Node next = node.next;
        previous.next = next;
        next.prev = previous;
        node.prev = null;
        node.next = null;
    }

    private void evictLeastRecentlyUsed() {
        Node lru = sentinelTail.prev;
        if (lru == sentinelHead) {
            return;
        }

        detach(lru);
        entries.remove(lru.key);
    }
}
