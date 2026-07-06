package euclidean;

import java.util.Map;

final class GcdProviderRegistry {

    private static final String DEFAULT = "iterative";

    private static final Map<String, GcdProvider> PROVIDERS = Map.of(
            "iterative", EuclideanAlgorithm.iterative(),
            "recursive", EuclideanAlgorithm.recursive()
    );

    private GcdProviderRegistry() {}

    static GcdProvider get(String name) {
        GcdProvider provider = PROVIDERS.get(name);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown GCD provider: " + name);
        }
        return provider;
    }

    static GcdProvider getDefault() {
        return PROVIDERS.get(DEFAULT);
    }
}
