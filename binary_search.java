class BinarySearch {
    public static void main(String[] args) {
        SearchType searchType = SearchType.BINARY;
        boolean loggingEnabled = false;
        for (String arg : args) {
            if ("log".equalsIgnoreCase(arg) || "logging".equalsIgnoreCase(arg)) {
                loggingEnabled = true;
            } else {
                searchType = SearchType.from(arg);
            }
        }

        ResultHandler resultHandler = new ConsoleResultHandler();
        SearchAlgorithm<Integer> searchAlgorithm = SearchAlgorithmFactory.create(
                searchType,
                loggingEnabled,
                resultHandler);
        Integer[] values = { 2, 3, 4, 10, 40 };
        Integer target = 10;
        SearchRequest<Integer> request = SearchRequest.<Integer>builder()
                .values(values)
                .target(target)
                .build();

        searchAlgorithm.search(request);
    }
}
