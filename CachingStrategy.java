import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decorator strategy that caches computation results based on input hash.
 */
public class CachingStrategy extends StrategyDecorator {
    private final Map<Integer, List<Long>> cache = new HashMap<>();

    public CachingStrategy(ComputationStrategy delegate) {
        super(delegate);
    }

    @Override
    public List<Long> compute(int[] arr) {
        int hash = hashArray(arr);
        if (cache.containsKey(hash)) {
            return cache.get(hash);
        }

        List<Long> result = delegate.compute(arr);
        cache.put(hash, result);
        return result;
    }

    private int hashArray(int[] arr) {
        int hash = 1;
        for (int value : arr) {
            hash = 31 * hash + value;
        }
        return hash;
    }

    public void clearCache() {
        cache.clear();
    }

    public int getCacheSize() {
        return cache.size();
    }

    @Override
    public String getName() {
        return "Cached " + delegate.getName();
    }
}
