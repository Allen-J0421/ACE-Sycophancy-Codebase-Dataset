package sorting.core;

import java.util.Comparator;

public class Sorter<T> {
    private final Comparator<T> comparator;
    private final SortStrategyFactory<T> strategyFactory;
    private final SortObserver observer;

    private Sorter(Builder<T> builder) {
        this.comparator = builder.comparator;
        this.strategyFactory = builder.strategyFactory;
        this.observer = builder.observer;
    }

    public void sort(T[] arr) {
        CountingComparator<T> counting = new CountingComparator<>(comparator);
        SortStrategy<T> strategy = strategyFactory.create(counting);
        observer.onSortStart();
        long start = System.nanoTime();
        strategy.sort(arr);
        observer.onSortComplete(System.nanoTime() - start, counting.getCount());
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Comparator<T> comparator;
        private SortStrategyFactory<T> strategyFactory;
        private SortObserver observer = (duration, comparisons) -> {};

        public Builder<T> comparator(Comparator<T> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Builder<T> strategy(SortStrategyFactory<T> strategyFactory) {
            this.strategyFactory = strategyFactory;
            return this;
        }

        public Builder<T> observer(SortObserver observer) {
            this.observer = observer;
            return this;
        }

        public Sorter<T> build() {
            if (comparator == null)
                throw new IllegalStateException("A comparator must be provided");
            if (strategyFactory == null)
                throw new IllegalStateException("A sort strategy must be provided");
            return new Sorter<>(this);
        }
    }
}
