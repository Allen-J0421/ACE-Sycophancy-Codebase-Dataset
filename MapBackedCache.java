import java.util.Map;
import java.util.Objects;

public abstract class MapBackedCache<K, V> extends AbstractCache<K, V> {
    private final Map<K, V> entries;

    protected MapBackedCache(int capacity, Map<K, V> entries) {
        super(capacity);
        this.entries = Objects.requireNonNull(entries, "entries must not be null");
    }

    @Override
    protected final V getValue(K key) {
        return entries.get(key);
    }

    @Override
    protected final boolean containsKeyValue(K key) {
        return entries.containsKey(key);
    }

    @Override
    protected final void putValue(K key, V value) {
        entries.put(key, value);
    }

    @Override
    protected final V removeValue(K key) {
        return entries.remove(key);
    }

    @Override
    public final int size() {
        return entries.size();
    }

    @Override
    public final void clear() {
        entries.clear();
    }
}
