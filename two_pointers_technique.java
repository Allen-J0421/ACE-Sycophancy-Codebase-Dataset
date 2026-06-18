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
}

/**
 * Two Pointers Algorithm implementation for finding pairs in sorted arrays.
 * Provides efficient algorithms for solving pair-finding problems with O(n) time complexity.
 */
class TwoPointersTechnique {

    /**
     * Finds a pair of indices in a sorted array that sum to the target value.
     * Uses the two-pointers technique with convergent pointers.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return IndexPair containing indices and values if found, null otherwise
     * Time: O(n), Space: O(1)
     */
    public static IndexPair findPair(int[] sortedArray, int targetSum) {
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
    public static boolean pairExists(int[] sortedArray, int targetSum) {
        return findPair(sortedArray, targetSum) != null;
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
        demonstratePairSearch();
        demonstrateExistenceCheck();
    }

    private static void demonstratePairSearch() {
        System.out.println("=== Pair Search Demo ===");
        IndexPair pair = TwoPointersTechnique.findPair(TEST_ARRAY, TARGET_SUM);

        if (pair != null) {
            System.out.println("Pair found: " + pair);
        } else {
            System.out.println("No pair found for sum: " + TARGET_SUM);
        }
    }

    private static void demonstrateExistenceCheck() {
        System.out.println("\n=== Existence Check Demo ===");
        boolean exists = TwoPointersTechnique.pairExists(TEST_ARRAY, TARGET_SUM);
        System.out.println("Pair exists: " + exists);
    }
}
