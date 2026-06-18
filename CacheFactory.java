public interface CacheFactory {
    <K, V> Cache<K, V> create(int capacity);
}
