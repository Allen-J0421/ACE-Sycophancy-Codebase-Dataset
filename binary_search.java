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

class SearchEngine<T extends Comparable<? super T>> {
    private final SearchStrategy<T> searchStrategy;
    private final SearchResultFormatter resultFormatter;

    private SearchEngine(SearchStrategy<T> searchStrategy, SearchResultFormatter resultFormatter) {
        this.searchStrategy = searchStrategy;
        this.resultFormatter = resultFormatter;
    }

    static <T extends Comparable<? super T>> Builder<T> builder() {
        return new Builder<>();
    }

    String search(T[] array, T target) {
        SearchContext<T> context = new SearchContext<>(array, target);
        SearchResult result = searchStrategy.search(context);

        return resultFormatter.format(result);
    }

    static class Builder<T extends Comparable<? super T>> {
        private SearchStrategy<T> searchStrategy = new BinarySearchStrategy<>();
        private SearchResultFormatter resultFormatter = new SearchResultFormatter();

        Builder<T> withSearchStrategy(SearchStrategy<T> searchStrategy) {
            this.searchStrategy = searchStrategy;
            return this;
        }

        Builder<T> withResultFormatter(SearchResultFormatter resultFormatter) {
            this.resultFormatter = resultFormatter;
            return this;
        }

        SearchEngine<T> build() {
            return new SearchEngine<>(searchStrategy, resultFormatter);
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
