interface SearchStrategy<T extends Comparable<? super T>> {
    SearchResult search(T[] array, T target);
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

class BinarySearchStrategy<T extends Comparable<? super T>> implements SearchStrategy<T> {
    @Override
    public SearchResult search(T[] array, T target) {
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
        SearchStrategy<Integer> searchStrategy = new BinarySearchStrategy<>();
        SearchResult result = searchStrategy.search(numbers, target);
        SearchResultFormatter formatter = new SearchResultFormatter();

        System.out.println(formatter.format(result));
    }
}
