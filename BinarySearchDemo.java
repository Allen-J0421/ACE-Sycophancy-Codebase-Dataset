final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    static void run() {
        run(SearchContainer.defaultContainer());
    }

    static void run(SearchAlgorithm<Integer> searchAlgorithm, SearchResultFormatter resultFormatter) {
        run(SearchContainer.<Integer>builder()
                .withSearchAlgorithm(searchAlgorithm)
                .withResultFormatter(resultFormatter)
                .build());
    }

    static void run(SearchContainer<Integer> container) {
        validateContainer(container);

        Integer[] sortedArray = { 2, 3, 4, 10, 40 };
        Integer target = 10;
        run(sortedArray, target, container);
    }

    static <T> void run(T[] sortedArray, T target, SearchContainer<T> container) {
        validateContainer(container);

        SearchResult result = container.searchAlgorithm().search(sortedArray, target);

        System.out.println(container.resultFormatter().format(result));
    }

    private static void validateContainer(SearchContainer<?> container) {
        if (container == null) {
            throw new IllegalArgumentException("container must not be null");
        }
    }
}
