import java.util.HashMap;
import java.util.Map;

class LRUCache {
    private static final int MISSING_VALUE = -1;

    private static class Node {
        private final int key;
        private int value;
        private Node next;
        private Node prev;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cacheMap;
    private final Node head;
    private final Node tail;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new HashMap<>();
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    int get(int key) {
        Node node = cacheMap.get(key);
        if (node == null) {
            return MISSING_VALUE;
        }

        moveToFront(node);
        return node.value;
    }

    void put(int key, int value) {
        Node node = cacheMap.get(key);
        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }

        node = new Node(key, value);
        cacheMap.put(key, node);
        addToFront(node);

        if (cacheMap.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

    private void addToFront(Node node) {
        Node nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev = node;
    }

    private void remove(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private void evictLeastRecentlyUsed() {
        Node nodeToDelete = tail.prev;
        remove(nodeToDelete);
        cacheMap.remove(nodeToDelete.key);
    }
}

class Main {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));
        cache.put(3, 3);
        System.out.println(cache.get(2));
        cache.put(4, 4);
        System.out.println(cache.get(1));
        System.out.println(cache.get(3));
        System.out.println(cache.get(4));
    }
}
