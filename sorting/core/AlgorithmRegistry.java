package sorting.core;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class AlgorithmRegistry {
    private static final Map<String, SortAlgorithmProvider> providers = new HashMap<>();

    static {
        ServiceLoader.load(SortAlgorithmProvider.class)
            .forEach(p -> providers.put(p.name(), p));
    }

    public static void register(SortAlgorithmProvider provider) {
        providers.put(provider.name(), provider);
    }

    public static <T> SortStrategyFactory<T> get(String name) {
        SortAlgorithmProvider provider = providers.get(name);
        if (provider == null)
            throw new IllegalArgumentException("No algorithm registered under name: " + name);
        return provider.factory();
    }
}
