import java.util.Objects;

/**
 * Represents a pair of indices and their corresponding values in an array.
 */
class IndexPair {
    public final int leftIndex;
    public final int rightIndex;
    public final int leftValue;
    public final int rightValue;

    IndexPair(int leftIndex, int rightIndex, int leftValue, int rightValue) {
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d] = %d + %d", leftIndex, rightIndex, leftValue, rightValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexPair)) return false;
        IndexPair pair = (IndexPair) o;
        return leftIndex == pair.leftIndex && rightIndex == pair.rightIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftIndex, rightIndex);
    }
}

/**
 * Strategy interface for pair-finding algorithms in sorted arrays.
 */
interface PairFinder {
    IndexPair findPair(int[] sortedArray, int targetSum);
}

/**
 * Two Pointers Algorithm implementation for finding pairs in sorted arrays.
 * Provides efficient algorithms for solving pair-finding problems with O(n) time complexity.
 */
class TwoPointersTechnique implements PairFinder {
    private static final TwoPointersTechnique INSTANCE = new TwoPointersTechnique();

    public static TwoPointersTechnique getInstance() {
        return INSTANCE;
    }

    /**
     * Finds a pair of indices in a sorted array that sum to the target value.
     * Uses the two-pointers technique with convergent pointers.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return IndexPair containing indices and values if found, null otherwise
     * Time: O(n), Space: O(1)
     */
    @Override
    public IndexPair findPair(int[] sortedArray, int targetSum) {
        IndexPair result = searchPair(sortedArray, targetSum, true);
        return result;
    }

    /**
     * Checks if a pair exists in a sorted array that sums to the target value.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return true if a pair exists, false otherwise
     * Time: O(n), Space: O(1)
     */
    public boolean pairExists(int[] sortedArray, int targetSum) {
        return findPair(sortedArray, targetSum) != null;
    }

    /**
     * Counts the number of pairs in a sorted array that sum to the target value.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return the count of pairs that sum to targetSum
     * Time: O(n), Space: O(1)
     */
    public int countPairs(int[] sortedArray, int targetSum) {
        searchPair(sortedArray, targetSum, false);
        return pairCount;
    }

    private int pairCount;

    private IndexPair searchPair(int[] sortedArray, int targetSum, boolean stopAtFirst) {
        pairCount = 0;

        if (!isValidArray(sortedArray)) {
            return null;
        }

        int left = 0;
        int right = sortedArray.length - 1;
        IndexPair foundPair = null;

        while (left < right) {
            int currentSum = sortedArray[left] + sortedArray[right];

            if (currentSum == targetSum) {
                foundPair = new IndexPair(left, right, sortedArray[left], sortedArray[right]);
                pairCount++;
                if (stopAtFirst) {
                    return foundPair;
                }
                left++;
                right--;
            } else if (currentSum < targetSum) {
                left++;
            } else {
                right--;
            }
        }

        return foundPair;
    }

    private static boolean isValidArray(int[] array) {
        return array != null && array.length >= 2;
    }
}

/**
 * Demonstration of the Two Pointers Technique algorithms.
 */
class TwoPointersTechniqueDemo {
    private static final TestCase[] TEST_CASES = {
        new TestCase(new int[]{-3, -1, 0, 1, 2}, -2),
        new TestCase(new int[]{1, 2, 3, 4, 5}, 7),
        new TestCase(new int[]{-5, -2, 0, 1, 3}, 1)
    };

    public static void main(String[] args) {
        TwoPointersTechnique technique = TwoPointersTechnique.getInstance();
        for (TestCase testCase : TEST_CASES) {
            printTestCase(technique, testCase);
        }
    }

    private static void printTestCase(TwoPointersTechnique technique, TestCase testCase) {
        System.out.println("\n=== Test Case: target = " + testCase.target + " ===");
        demonstratePairSearch(technique, testCase.array, testCase.target);
        demonstrateExistenceCheck(technique, testCase.array, testCase.target);
        demonstratePairCounting(technique, testCase.array, testCase.target);
    }

    private static void demonstratePairSearch(TwoPointersTechnique technique, int[] array, int target) {
        IndexPair pair = technique.findPair(array, target);
        System.out.println("Pair: " + (pair != null ? pair : "not found"));
    }

    private static void demonstrateExistenceCheck(TwoPointersTechnique technique, int[] array, int target) {
        System.out.println("Exists: " + technique.pairExists(array, target));
    }

    private static void demonstratePairCounting(TwoPointersTechnique technique, int[] array, int target) {
        System.out.println("Count: " + technique.countPairs(array, target));
    }

    private static class TestCase {
        final int[] array;
        final int target;

        TestCase(int[] array, int target) {
            this.array = array;
            this.target = target;
        }
    }
}
