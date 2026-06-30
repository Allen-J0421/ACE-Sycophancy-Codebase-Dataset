import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import search.SearchAlgorithm;
import search.SearchResult;
import search.SearchService;
import search.SearchServiceBuilder;
import search.SearchStrategyFactory;

class BinarySearchDemo {
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(2, 3, 4, 10, 40);
        int target = 10;

        SearchAlgorithm<Integer> algorithm = SearchStrategyFactory.linearSearch();
        SearchService<Integer> searchService = SearchServiceBuilder.of(algorithm)
                .preValidation(BinarySearchDemo::validateValues)
                .build();
        SearchResult result = searchService.search(values, target);
        String message = result.map(
                index -> "Element is present at index " + index,
                () -> "Element is not present in array");

        System.out.println(message);
    }

    private static void validateValues(List<Integer> values, Integer target) {
        Objects.requireNonNull(target, "target must not be null");

        if (values.contains(null)) {
            throw new IllegalArgumentException("values must not contain null");
        }
    }
}
