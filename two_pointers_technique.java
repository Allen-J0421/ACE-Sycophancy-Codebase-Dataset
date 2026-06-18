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

    /**
     * Gets the singleton instance of TwoPointersTechnique.
     */
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
        if (!isValidArray(sortedArray)) {
            return null;
        }

        int left = 0;
        int right = sortedArray.length - 1;

        while (left < right) {
            int currentSum = sortedArray[left] + sortedArray[right];

            if (currentSum == targetSum) {
                return new IndexPair(left, right, sortedArray[left], sortedArray[right]);
            }

            if (currentSum < targetSum) {
                left++;
            } else {
                right--;
            }
        }

        return null;
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
        if (!isValidArray(sortedArray)) {
            return 0;
        }

        int count = 0;
        int left = 0;
        int right = sortedArray.length - 1;

        while (left < right) {
            int currentSum = sortedArray[left] + sortedArray[right];

            if (currentSum == targetSum) {
                count++;
                left++;
                right--;
            } else if (currentSum < targetSum) {
                left++;
            } else {
                right--;
            }
        }

        return count;
    }

    private static boolean isValidArray(int[] array) {
        return array != null && array.length >= 2;
    }
}

/**
 * Demonstration of the Two Pointers Technique algorithms.
 */
class TwoPointersTechniqueDemo {
    private static final int[] TEST_ARRAY = {-3, -1, 0, 1, 2};
    private static final int TARGET_SUM = -2;

    public static void main(String[] args) {
        PairFinder finder = TwoPointersTechnique.getInstance();
        runDemonstrations(finder);
    }

    private static void runDemonstrations(PairFinder finder) {
        demonstratePairSearch(finder);
        demonstrateExistenceCheck(finder);
        demonstratePairCounting(finder);
    }

    private static void demonstratePairSearch(PairFinder finder) {
        System.out.println("=== Pair Search Demo ===");
        IndexPair pair = finder.findPair(TEST_ARRAY, TARGET_SUM);

        if (pair != null) {
            System.out.println("Pair found: " + pair);
        } else {
            System.out.println("No pair found for sum: " + TARGET_SUM);
        }
    }

    private static void demonstrateExistenceCheck(PairFinder finder) {
        System.out.println("\n=== Existence Check Demo ===");
        boolean exists = TwoPointersTechnique.getInstance().pairExists(TEST_ARRAY, TARGET_SUM);
        System.out.println("Pair exists: " + exists);
    }

    private static void demonstratePairCounting(PairFinder finder) {
        System.out.println("\n=== Pair Counting Demo ===");
        int count = TwoPointersTechnique.getInstance().countPairs(TEST_ARRAY, TARGET_SUM);
        System.out.println("Number of pairs: " + count);
    }
}
