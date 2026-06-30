import java.util.Arrays;
import java.util.List;
import search.DefaultSearchService;
import search.LinearSearchAlgorithm;
import search.SearchAlgorithm;
import search.SearchResult;
import search.SearchService;

class BinarySearchDemo {
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(2, 3, 4, 10, 40);
        int target = 10;

        SearchAlgorithm<Integer> algorithm = new LinearSearchAlgorithm<Integer>();
        SearchService<Integer> searchService = new DefaultSearchService<Integer>(algorithm);
        SearchResult result = searchService.search(values, target);
        String message = result.map(
                index -> "Element is present at index " + index,
                () -> "Element is not present in array");

        System.out.println(message);
    }
}
