import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

interface SearchStrategy<T extends Comparable<? super T>> {
    SearchResult search(SearchContext<T> context);
}

class SearchContext<T extends Comparable<? super T>> {
    private final T[] array;
    private final T target;

    SearchContext(T[] array, T target) {
        this.array = array;
        this.target = target;
    }

    T[] getArray() {
        return array;
    }

    T getTarget() {
        return target;
    }
}

class SearchResult {
    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return new SearchResult(-1);
    }

    boolean isFound() {
        return index >= 0;
    }

    int getIndex() {
        return index;
    }
}

class SearchResultFormatter {
    String format(SearchResult result) {
        if (!result.isFound()) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result.getIndex();
    }
}

interface SearchObserver<T extends Comparable<? super T>> {
    void beforeSearch(SearchContext<T> context);

    void afterSearch(SearchContext<T> context, SearchResult result);
}

class SearchStrategyFactory<T extends Comparable<? super T>> {
    static final String BINARY_SEARCH = "binary";

    private final Map<String, Supplier<SearchStrategy<T>>> strategySuppliers = new HashMap<>();

    SearchStrategyFactory() {
        registerStrategy(BINARY_SEARCH, () -> new BinarySearchStrategy<>());
    }

    SearchStrategyFactory<T> registerStrategy(String key, Supplier<SearchStrategy<T>> supplier) {
        strategySuppliers.put(key, supplier);
        return this;
    }

    SearchStrategy<T> createStrategy(String key) {
        Supplier<SearchStrategy<T>> strategySupplier = strategySuppliers.get(key);

        if (strategySupplier == null) {
            throw new IllegalArgumentException("Unsupported search strategy: " + key);
        }

        return strategySupplier.get();
    }
}

class SearchEngine<T extends Comparable<? super T>> {
    private final SearchStrategyFactory<T> searchStrategyFactory;
    private final String searchStrategyKey;
    private final SearchResultFormatter resultFormatter;
    private final List<SearchObserver<T>> searchObservers;

    private SearchEngine(
            SearchStrategyFactory<T> searchStrategyFactory,
            String searchStrategyKey,
            SearchResultFormatter resultFormatter,
            List<SearchObserver<T>> searchObservers) {
        this.searchStrategyFactory = searchStrategyFactory;
        this.searchStrategyKey = searchStrategyKey;
        this.resultFormatter = resultFormatter;
        this.searchObservers = new ArrayList<>(searchObservers);
    }

    static <T extends Comparable<? super T>> Builder<T> builder() {
        return new Builder<>();
    }

    String search(T[] array, T target) {
        SearchContext<T> context = new SearchContext<>(array, target);
        SearchStrategy<T> searchStrategy = searchStrategyFactory.createStrategy(searchStrategyKey);

        for (SearchObserver<T> searchObserver : searchObservers) {
            searchObserver.beforeSearch(context);
        }

        SearchResult result = searchStrategy.search(context);

        for (SearchObserver<T> searchObserver : searchObservers) {
            searchObserver.afterSearch(context, result);
        }

        return resultFormatter.format(result);
    }

    static class Builder<T extends Comparable<? super T>> {
        private SearchStrategyFactory<T> searchStrategyFactory = new SearchStrategyFactory<>();
        private String searchStrategyKey = SearchStrategyFactory.BINARY_SEARCH;
        private SearchResultFormatter resultFormatter = new SearchResultFormatter();
        private final List<SearchObserver<T>> searchObservers = new ArrayList<>();

        Builder<T> withSearchStrategyFactory(SearchStrategyFactory<T> searchStrategyFactory) {
            this.searchStrategyFactory = searchStrategyFactory;
            return this;
        }

        Builder<T> withSearchStrategyKey(String searchStrategyKey) {
            this.searchStrategyKey = searchStrategyKey;
            return this;
        }

        Builder<T> withResultFormatter(SearchResultFormatter resultFormatter) {
            this.resultFormatter = resultFormatter;
            return this;
        }

        Builder<T> addSearchObserver(SearchObserver<T> searchObserver) {
            this.searchObservers.add(searchObserver);
            return this;
        }

        Builder<T> withSearchObserver(SearchObserver<T> searchObserver) {
            return addSearchObserver(searchObserver);
        }

        Builder<T> withSearchObservers(List<SearchObserver<T>> searchObservers) {
            this.searchObservers.clear();
            this.searchObservers.addAll(searchObservers);
            return this;
        }

        SearchEngine<T> build() {
            return new SearchEngine<>(searchStrategyFactory, searchStrategyKey, resultFormatter, searchObservers);
        }
    }
}

class BinarySearchStrategy<T extends Comparable<? super T>> implements SearchStrategy<T> {
    @Override
    public SearchResult search(SearchContext<T> context) {
        T[] array = context.getArray();
        T target = context.getTarget();
        int low = 0, high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(target);

            if (comparison == 0) {
                return SearchResult.foundAt(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }
}

class BinarySearch {
    public static void main(String[] args) {
        Integer[] numbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        SearchEngine<Integer> searchEngine = SearchEngine.<Integer>builder().build();

        System.out.println(searchEngine.search(numbers, target));
    }
}
