import java.util.HashMap;
import java.util.Map;

class Node<K, V> {
    K key;
    V value;
    Node<K, V> next;
    Node<K, V> prev;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class DoublyLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;

    DoublyLinkedList() {
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    void addFirst(Node<K, V> node) {
        Node<K, V> nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev = node;
    }

    void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    Node<K, V> getLast() {
        return tail.prev;
    }
}

class LRUCache<K, V> {
    private int capacity;
    private Map<K, Node<K, V>> cacheMap;
    private DoublyLinkedList<K, V> list;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new HashMap<>();
        this.list = new DoublyLinkedList<>();
    }

    V get(K key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }

        Node<K, V> node = cacheMap.get(key);
        list.remove(node);
        list.addFirst(node);
        return node.value;
    }

    void put(K key, V value) {
        if (cacheMap.containsKey(key)) {
            list.remove(cacheMap.get(key));
        }

        Node<K, V> node = new Node<>(key, value);
        cacheMap.put(key, node);
        list.addFirst(node);

        if (cacheMap.size() > capacity) {
            Node<K, V> nodeToDelete = list.getLast();
            list.remove(nodeToDelete);
            cacheMap.remove(nodeToDelete.key);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);

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
