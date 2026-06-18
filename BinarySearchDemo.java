/**
 * Demonstration and example usage of BinarySearch.
 * Separated from the algorithm implementation for clarity.
 */
class BinarySearchDemo {
    public static void main(String[] args) {
        runBasicSearch();
        runSearchWithStats();
        runFirstOccurrence();
    }

    private static void runBasicSearch() {
        System.out.println("=== Basic Search ===");
        SearchEngine<Integer> searcher = new SearchEngine<>();
        Integer[] array = {2, 3, 4, 10, 40};
        Integer target = 10;

        SearchResult result = searcher.search(array, target);
        printResult(result);
    }

    private static void runSearchWithStats() {
        System.out.println("\n=== Search with Statistics ===");
        SearchEngine<Integer> searcher = new SearchEngine<Integer>().withStats();
        Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer target = 8;

        SearchResult result = searcher.search(array, target);
        printResult(result);
    }

    private static void runFirstOccurrence() {
        System.out.println("\n=== Find First Occurrence ===");
        SearchEngine<Integer> searcher = new SearchEngine<Integer>().withStats();
        Integer[] array = {1, 2, 2, 2, 3, 4, 5};
        Integer target = 2;

        SearchResult result = searcher.searchFirst(array, target);
        if (result.isFound()) {
            System.out.println("First occurrence at index: " + result.getIndex());
        } else {
            System.out.println("Element not found");
        }

        if (result instanceof SearchResultWithStats) {
            SearchStats stats = ((SearchResultWithStats) result).getStats();
            System.out.println("Statistics: " + stats);
        }
    }

    private static void printResult(SearchResult result) {
        if (result.isFound()) {
            System.out.println("Element found at index: " + result.getIndex());
        } else {
            System.out.println("Element not found");
        }

        if (result instanceof SearchResultWithStats) {
            SearchStats stats = ((SearchResultWithStats) result).getStats();
            System.out.println("Statistics: " + stats);
        }
    }
}
