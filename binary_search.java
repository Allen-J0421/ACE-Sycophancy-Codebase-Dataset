class BinarySearch {
    public static void main(String[] args) {
        SearchAlgorithm<Integer> searchAlgorithm = new BinarySearchService<>();
        Integer[] values = { 2, 3, 4, 10, 40 };
        Integer target = 10;
        SearchRequest<Integer> request = SearchRequest.<Integer>builder()
                .values(values)
                .target(target)
                .build();

        SearchResult result = searchAlgorithm.search(request);
        if (!result.isFound()) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result.index());
        }
    }
}
