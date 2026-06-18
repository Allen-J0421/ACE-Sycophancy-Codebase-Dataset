import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private static final int NOT_FOUND = -1;

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final UsageList usageList;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.usageList = new UsageList();
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return NOT_FOUND;
        }

        usageList.moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node existingNode = cache.get(key);
        if (existingNode != null) {
            existingNode.value = value;
            usageList.moveToFront(existingNode);
            return;
        }

        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        usageList.addFirst(newNode);

        if (cache.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    public int size() {
        return cache.size();
    }

    private void evictLeastRecentlyUsed() {
        Node leastRecentlyUsed = usageList.removeLast();
        if (leastRecentlyUsed != null) {
            cache.remove(leastRecentlyUsed.key);
        }
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

    private static final class UsageList {
        private final Node head = new Node(0, 0);
        private final Node tail = new Node(0, 0);

        private UsageList() {
            head.next = tail;
            tail.prev = head;
        }

        private void addFirst(Node node) {
            insertBetween(head, node, head.next);
        }

        private void moveToFront(Node node) {
            unlink(node);
            addFirst(node);
        }

        private Node removeLast() {
            Node leastRecentlyUsed = tail.prev;
            if (leastRecentlyUsed == head) {
                return null;
            }

            unlink(leastRecentlyUsed);
            return leastRecentlyUsed;
        }

        private void insertBetween(Node previous, Node node, Node next) {
            previous.next = node;
            node.prev = previous;
            node.next = next;
            next.prev = node;
        }

        private void unlink(Node node) {
            Node previous = node.prev;
            Node next = node.next;
            previous.next = next;
            next.prev = previous;
            node.prev = null;
            node.next = null;
        }
    }
}
