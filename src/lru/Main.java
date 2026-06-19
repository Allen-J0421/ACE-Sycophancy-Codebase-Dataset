package lru;

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
