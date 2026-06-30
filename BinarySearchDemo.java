final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    static void run() {
        run(SearchContainer.defaultContainer());
    }

    static void run(SearchAlgorithm searchAlgorithm, SearchResultFormatter resultFormatter) {
        run(SearchContainer.builder()
                .withSearchAlgorithm(searchAlgorithm)
                .withResultFormatter(resultFormatter)
                .build());
    }

    static void run(SearchContainer container) {
        validateContainer(container);

        int[] sortedArray = { 2, 3, 4, 10, 40 };
        int target = 10;
        SearchResult result = container.searchAlgorithm().search(sortedArray, target);

        System.out.println(container.resultFormatter().format(result));
    }

    private static void validateContainer(SearchContainer container) {
        if (container == null) {
            throw new IllegalArgumentException("container must not be null");
        }
    }
}
