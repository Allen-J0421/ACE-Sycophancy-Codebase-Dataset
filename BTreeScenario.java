import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class BTreeScenario {
    private final int minDegree;
    private final List<Integer> insertedKeys;
    private final List<Integer> expectedKeys;
    private final List<Integer> presentKeys;
    private final List<Integer> missingKeys;

    private BTreeScenario(
            int minDegree,
            List<Integer> insertedKeys,
            List<Integer> expectedKeys,
            List<Integer> presentKeys,
            List<Integer> missingKeys) {
        this.minDegree = minDegree;
        this.insertedKeys = List.copyOf(insertedKeys);
        this.expectedKeys = List.copyOf(expectedKeys);
        this.presentKeys = List.copyOf(presentKeys);
        this.missingKeys = List.copyOf(missingKeys);
    }

    static BTreeScenario fromKeys(int minDegree, int[] insertedKeys, int[] presentKeys, int[] missingKeys) {
        List<Integer> insertedKeyList = toList(insertedKeys);
        return new BTreeScenario(
                minDegree,
                insertedKeyList,
                sortedCopy(insertedKeyList),
                toList(presentKeys),
                toList(missingKeys)
        );
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

    List<Integer> presentKeys() {
        return presentKeys;
    }

    List<Integer> missingKeys() {
        return missingKeys;
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
}
