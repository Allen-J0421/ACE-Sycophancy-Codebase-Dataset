import java.util.Objects;

/**
 * Represents a pair of indices and their corresponding values in an array.
 */
class IndexPair {
    private final int leftIndex;
    private final int rightIndex;
    private final int leftValue;
    private final int rightValue;

    IndexPair(int leftIndex, int rightIndex, int leftValue, int rightValue) {
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    int getLeftIndex() {
        return leftIndex;
    }

    int getRightIndex() {
        return rightIndex;
    }

    int getLeftValue() {
        return leftValue;
    }

    int getRightValue() {
        return rightValue;
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
 * Results from a pair search operation.
 */
class SearchResult {
    private final IndexPair pair;
    private final int count;

    SearchResult(IndexPair pair, int count) {
        this.pair = pair;
        this.count = count;
    }

    IndexPair getPair() {
        return pair;
    }

    int getCount() {
        return count;
    }

    boolean exists() {
        return pair != null;
    }

    @Override
    public String toString() {
        return String.format("SearchResult(pair=%s, count=%d)",
            exists() ? pair : "null", count);
    }
}

/**
 * Validator for search inputs.
 */
interface ArrayValidator {
    boolean isValid(int[] array);
}

/**
 * Default validator for array inputs requiring minimum length.
 */
class DefaultArrayValidator implements ArrayValidator {
    private static final int MIN_LENGTH = 2;

    @Override
    public boolean isValid(int[] array) {
        return array != null && array.length >= MIN_LENGTH;
    }
}

/**
 * Performs search operations on sorted arrays.
 */
interface SearchOperation {
    SearchResult search(int[] array, int targetSum);
}

/**
 * Formats output for test results.
 */
interface ResultFormatter {
    String format(int target, SearchResult result);
}

/**
 * Default formatter for displaying search results.
 */
class DefaultResultFormatter implements ResultFormatter {
    @Override
    public String format(int target, SearchResult result) {
        return String.format("\n=== Target: %d ===\n%s", target, result);
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
class TwoPointersTechnique implements PairFinder, SearchOperation {
    private static final TwoPointersTechnique INSTANCE = new TwoPointersTechnique();
    private static final ArrayValidator DEFAULT_VALIDATOR = new DefaultArrayValidator();

    private TwoPointersTechnique() {
    }

    public static TwoPointersTechnique getInstance() {
        return INSTANCE;
    }

    /**
     * Finds a pair of indices in a sorted array that sum to the target value.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return IndexPair containing indices and values if found, null otherwise
     * Time: O(n), Space: O(1)
     */
    @Override
    public IndexPair findPair(int[] sortedArray, int targetSum) {
        return search(sortedArray, targetSum, true).getPair();
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
        return search(sortedArray, targetSum, false).getCount();
    }

    /**
     * Performs a comprehensive search returning both first pair and count.
     *
     * @param sortedArray a sorted integer array
     * @param targetSum   the target sum to find
     * @return SearchResult containing pair and count
     */
    @Override
    public SearchResult search(int[] array, int targetSum) {
        return search(array, targetSum, false);
    }

    /**
     * Performs a comprehensive search with stop-at-first option.
     *
     * @param sortedArray  a sorted integer array
     * @param targetSum    the target sum to find
     * @param stopAtFirst  if true, return after finding first pair
     * @return SearchResult containing pair and count
     */
    public SearchResult search(int[] sortedArray, int targetSum, boolean stopAtFirst) {
        return search(sortedArray, targetSum, stopAtFirst, DEFAULT_VALIDATOR);
    }

    /**
     * Performs a search with custom validation.
     *
     * @param sortedArray  a sorted integer array
     * @param targetSum    the target sum to find
     * @param stopAtFirst  if true, return after finding first pair
     * @param validator    custom array validator
     * @return SearchResult containing pair and count
     */
    public SearchResult search(int[] sortedArray, int targetSum, boolean stopAtFirst, ArrayValidator validator) {
        if (!validator.isValid(sortedArray)) {
            return new SearchResult(null, 0);
        }

        SearchState state = new SearchState(sortedArray.length);
        while (state.canContinue()) {
            int currentSum = sortedArray[state.left] + sortedArray[state.right];
            processSum(state, currentSum, targetSum, stopAtFirst, sortedArray);
        }

        return state.buildResult();
    }

    private void processSum(SearchState state, int currentSum, int targetSum, boolean stopAtFirst, int[] array) {
        if (currentSum == targetSum) {
            state.recordMatch(array, stopAtFirst);
        } else if (currentSum < targetSum) {
            state.moveLeftPointer();
        } else {
            state.moveRightPointer();
        }
    }

    /**
     * Encapsulates search state and operations.
     */
    private static class SearchState {
        int left;
        int right;
        IndexPair foundPair;
        int count;

        SearchState(int arrayLength) {
            this.left = 0;
            this.right = arrayLength - 1;
            this.count = 0;
        }

        boolean canContinue() {
            return left < right;
        }

        void moveLeftPointer() {
            left++;
        }

        void moveRightPointer() {
            right--;
        }

        void recordMatch(int[] array, boolean stopAtFirst) {
            foundPair = new IndexPair(left, right, array[left], array[right]);
            count++;
            if (!stopAtFirst) {
                moveLeftPointer();
                moveRightPointer();
            }
        }

        SearchResult buildResult() {
            return new SearchResult(foundPair, count);
        }
    }
}

/**
 * Test case with array and target sum.
 */
class TestCase {
    final int[] array;
    final int target;

    TestCase(int[] array, int target) {
        this.array = array;
        this.target = target;
    }
}

/**
 * Executes and displays test results for search operations.
 */
class TestRunner {
    private final SearchOperation searchOp;
    private final TestCase[] testCases;
    private final ResultFormatter formatter;

    TestRunner(SearchOperation searchOp, TestCase[] testCases) {
        this(searchOp, testCases, new DefaultResultFormatter());
    }

    TestRunner(SearchOperation searchOp, TestCase[] testCases, ResultFormatter formatter) {
        this.searchOp = searchOp;
        this.testCases = testCases;
        this.formatter = formatter;
    }

    void runAll() {
        for (TestCase testCase : testCases) {
            runCase(testCase);
        }
    }

    private void runCase(TestCase testCase) {
        SearchResult result = searchOp.search(testCase.array, testCase.target);
        System.out.println(formatter.format(testCase.target, result));
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
        SearchOperation searchOp = TwoPointersTechnique.getInstance();
        TestRunner runner = new TestRunner(searchOp, TEST_CASES);
        runner.runAll();
    }
}
