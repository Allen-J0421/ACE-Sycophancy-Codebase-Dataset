import java.util.HashMap;
import java.util.Map;

class Node {
    int key;
    int value;
    Node next;
    Node prev;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class DoublyLinkedList {
    private Node head;
    private Node tail;

    DoublyLinkedList() {
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    void addFirst(Node node) {
        Node nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev = node;
    }

    void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    Node getLast() {
        return tail.prev;
    }
}

class LRUCache {
    private int capacity;
    private Map<Integer, Node> cacheMap;
    private DoublyLinkedList list;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new HashMap<>();
        this.list = new DoublyLinkedList();
    }

    int get(int key) {
        if (!cacheMap.containsKey(key)) {
            return -1;
        }

        Node node = cacheMap.get(key);
        list.remove(node);
        list.addFirst(node);
        return node.value;
    }

    void put(int key, int value) {
        if (cacheMap.containsKey(key)) {
            list.remove(cacheMap.get(key));
        }

        Node node = new Node(key, value);
        cacheMap.put(key, node);
        list.addFirst(node);

        if (cacheMap.size() > capacity) {
            Node nodeToDelete = list.getLast();
            list.remove(nodeToDelete);
            cacheMap.remove(nodeToDelete.key);
        }
    }
}

public class Main {
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
