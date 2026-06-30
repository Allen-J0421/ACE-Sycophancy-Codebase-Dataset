final class BinarySearchDemo {
    private static final SearchAlgorithm SEARCH_ALGORITHM = new BinarySearcher();
    private static final SearchResultFormatter RESULT_FORMATTER = new DefaultSearchResultFormatter();

    private BinarySearchDemo() {
    }

    static void run() {
        run(SEARCH_ALGORITHM, RESULT_FORMATTER);
    }

    static void run(SearchAlgorithm searchAlgorithm, SearchResultFormatter resultFormatter) {
        validateDependencies(searchAlgorithm, resultFormatter);

        int[] sortedArray = { 2, 3, 4, 10, 40 };
        int target = 10;
        SearchResult result = searchAlgorithm.search(sortedArray, target);

        System.out.println(resultFormatter.format(result));
    }

    private static void validateDependencies(
            SearchAlgorithm searchAlgorithm,
            SearchResultFormatter resultFormatter) {
        if (searchAlgorithm == null) {
            throw new IllegalArgumentException("searchAlgorithm must not be null");
        }

        if (resultFormatter == null) {
            throw new IllegalArgumentException("resultFormatter must not be null");
        }
    }
}
