import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class BTreeScenario {
    private final int minDegree;
    private final List<Integer> insertedKeys;
    private final List<Integer> expectedKeys;
    private final List<SearchExpectation> searchExpectations;

    private BTreeScenario(int minDegree, List<Integer> insertedKeys, List<SearchExpectation> searchExpectations) {
        this.minDegree = minDegree;
        this.insertedKeys = List.copyOf(insertedKeys);
        this.expectedKeys = sortedCopy(insertedKeys);
        this.searchExpectations = List.copyOf(searchExpectations);
    }

    static BTreeScenario fromKeys(int minDegree, int[] insertedKeys, SearchExpectation... searchExpectations) {
        return new BTreeScenario(minDegree, toList(insertedKeys), List.of(searchExpectations));
    }

    BTree createTree() {
        return BTree.fromKeys(minDegree, insertedKeys);
    }

    int minDegree() {
        return minDegree;
    }

    List<Integer> insertedKeys() {
        return insertedKeys;
    }

    List<Integer> expectedKeys() {
        return expectedKeys;
    }

    List<SearchExpectation> searchExpectations() {
        return searchExpectations;
    }

    static SearchExpectation present(int key) {
        return new SearchExpectation(key, true);
    }

    static SearchExpectation missing(int key) {
        return new SearchExpectation(key, false);
    }

    private static List<Integer> sortedCopy(List<Integer> values) {
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        return sorted;
    }

    private static List<Integer> toList(int[] values) {
        List<Integer> result = new ArrayList<>(values.length);
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    static final class SearchExpectation {
        private final int key;
        private final boolean present;

        private SearchExpectation(int key, boolean present) {
            this.key = key;
            this.present = present;
        }

        int key() {
            return key;
        }

        boolean isPresent() {
            return present;
        }
    }
}
