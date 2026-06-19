package lru;

public class Main {
    public static void main(String[] args) {
        // Same workload, different eviction strategies — selected by composition,
        // not by changing the cache.
        System.out.println("LRU eviction:");
        runWorkload(new LRUCache<>(2));

        System.out.println("FIFO eviction:");
        runWorkload(new EvictingCache<>(2, new FifoEvictionPolicy<>()));
    }

    private static void runWorkload(Cache<Integer, Integer> cache) {
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.getOrDefault(1, -1));
        cache.put(3, 3);
        System.out.println(cache.getOrDefault(2, -1));
        cache.put(4, 4);
        System.out.println(cache.getOrDefault(1, -1));
        System.out.println(cache.getOrDefault(3, -1));
        System.out.println(cache.getOrDefault(4, -1));
    }
}
