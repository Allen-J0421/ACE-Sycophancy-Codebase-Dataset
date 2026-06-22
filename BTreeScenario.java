import java.util.ArrayList;
import java.util.List;

final class BTreeScenario {
    private final int minDegree;
    private final int[] insertedKeys;
    private final List<Integer> expectedKeys;
    private final int[] presentKeys;
    private final int[] missingKeys;

    BTreeScenario(int minDegree, int[] insertedKeys, List<Integer> expectedKeys, int[] presentKeys, int[] missingKeys) {
        this.minDegree = minDegree;
        this.insertedKeys = insertedKeys.clone();
        this.expectedKeys = List.copyOf(expectedKeys);
        this.presentKeys = presentKeys.clone();
        this.missingKeys = missingKeys.clone();
    }

    BTree createTree() {
        return BTree.fromKeys(minDegree, insertedKeys);
    }

    int minDegree() {
        return minDegree;
    }

    int[] insertedKeys() {
        return insertedKeys.clone();
    }

    List<Integer> expectedKeys() {
        return expectedKeys;
    }

    List<Integer> presentKeys() {
        return toList(presentKeys);
    }

    List<Integer> missingKeys() {
        return toList(missingKeys);
    }

    private static List<Integer> toList(int[] values) {
        List<Integer> result = new ArrayList<>(values.length);
        for (int value : values) {
            result.add(value);
        }
        return result;
    }
}
