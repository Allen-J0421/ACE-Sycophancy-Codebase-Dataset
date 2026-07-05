package sorting.core;

public interface SortAlgorithmProvider {
    String name();
    <T> SortStrategyFactory<T> factory();
}
