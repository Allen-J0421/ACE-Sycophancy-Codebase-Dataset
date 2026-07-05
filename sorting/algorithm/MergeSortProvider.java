package sorting.algorithm;

import sorting.core.SortAlgorithmProvider;
import sorting.core.SortStrategyFactory;

public class MergeSortProvider implements SortAlgorithmProvider {
    @Override
    public String name() { return "merge"; }

    @Override
    public <T> SortStrategyFactory<T> factory() {
        return c -> new MergeSort<>(c);
    }
}
