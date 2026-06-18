import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCache<K, V> implements Cache<K, V> {
    private final int capacity;

    protected AbstractCache(int capacity) {
        this.capacity = validateCapacity(capacity);
    }

    @Override
    public final Optional<V> get(K key) {
        return Optional.ofNullable(getValue(requireKey(key)));
    }

    @Override
    public final boolean containsKey(K key) {
        return containsKeyValue(requireKey(key));
    }

    @Override
    public final void put(K key, V value) {
        putValue(requireKey(key), requireValue(value));
    }

    @Override
    public final Optional<V> remove(K key) {
        return Optional.ofNullable(removeValue(requireKey(key)));
    }

    @Override
    public final int capacity() {
        return capacity;
    }

    protected abstract V getValue(K key);

    protected abstract boolean containsKeyValue(K key);

    protected abstract void putValue(K key, V value);

    protected abstract V removeValue(K key);

    protected final K requireKey(K key) {
        return Objects.requireNonNull(key, "key must not be null");
    }

    protected final V requireValue(V value) {
        return Objects.requireNonNull(value, "value must not be null");
    }

    private static int validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        return capacity;
    }
}
