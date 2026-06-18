package sorting.core;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class SortableProcessor {
    public static <T extends Comparable<T>> SortConfig<T> processSortable(Class<T> clazz) throws SortConfigurationException {
        if (!clazz.isAnnotationPresent(Sortable.class)) {
            throw new SortConfigurationException("Class " + clazz.getName() + " is not annotated with @Sortable");
        }

        Sortable annotation = clazz.getAnnotation(Sortable.class);

        if (annotation.value().isEmpty()) {
            throw new SortConfigurationException("Sortable annotation must have a value");
        }

        return extractConfig(annotation);
    }

    public static <T extends Comparable<T>> SortConfig<T> processSortable(Sortable annotation) throws SortConfigurationException {
        return extractConfig(annotation);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> SortConfig<T> extractConfig(Sortable annotation) throws SortConfigurationException {
        try {
            SortConfig.Builder<T> builder = (SortConfig.Builder<T>) SortConfig.builder();
            return builder
                    .comparator((Comparable::compareTo))
                    .insertionThreshold(annotation.insertionThreshold())
                    .trackMetrics(annotation.enableMetrics())
                    .validateInput(annotation.enableValidation())
                    .threadPoolSize(Math.max(1, Runtime.getRuntime().availableProcessors() / 2))
                    .build();
        } catch (SortConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new SortConfigurationException("Failed to process @Sortable annotation", e);
        }
    }

    public static String getAlgorithmName(Sortable annotation) {
        return annotation.algorithm();
    }

    public static boolean shouldEnableCaching(Sortable annotation) {
        return annotation.enableCaching();
    }

    public static int getCacheSize(Sortable annotation) {
        return annotation.cacheSize();
    }
}
