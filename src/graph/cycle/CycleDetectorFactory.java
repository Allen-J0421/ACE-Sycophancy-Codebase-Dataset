package graph.cycle;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;

public class CycleDetectorFactory {
    private static final Map<Algorithm, CycleDetectorProvider> REGISTRY = buildRegistry();

    private CycleDetectorFactory() {}

    private static Map<Algorithm, CycleDetectorProvider> buildRegistry() {
        Map<Algorithm, CycleDetectorProvider> map = new EnumMap<>(Algorithm.class);
        for (CycleDetectorProvider provider : ServiceLoader.load(CycleDetectorProvider.class)) {
            map.put(provider.algorithm(), provider);
        }
        return Collections.unmodifiableMap(map);
    }

    public static CycleDetector create(Algorithm algorithm) {
        CycleDetectorProvider provider = REGISTRY.get(algorithm);
        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for: " + algorithm);
        }
        return provider.create();
    }
}
