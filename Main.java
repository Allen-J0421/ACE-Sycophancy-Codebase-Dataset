public class Main {
    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);

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
