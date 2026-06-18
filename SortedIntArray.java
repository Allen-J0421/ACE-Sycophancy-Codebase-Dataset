import java.util.Arrays;
import java.util.Optional;

public final class SortedIntArray {

    private static final SortedIntArray EMPTY = new SortedIntArray(new int[0]);

    private final int[] values;

    private SortedIntArray(int[] values) {
        this.values = values;
    }

    public static SortedIntArray copyOf(int[] values) {
        validateSorted(values);
        if (values.length == 0) {
            return EMPTY;
        }

        return new SortedIntArray(Arrays.copyOf(values, values.length));
    }

    public static SortedIntArray of(int... values) {
        return copyOf(values);
    }

    public int size() {
        return values.length;
    }

    public Optional<PairMatch> findPairWithSum(long target) {
        SearchResult result = search(target);
        if (!result.found()) {
            return Optional.empty();
        }

        return Optional.of(result.toPairMatch(values));
    }

    public boolean hasPairWithSum(long target) {
        return search(target).found();
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SortedIntArray that)) {
            return false;
        }

        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    private boolean canContainPairMatching(long target) {
        return values.length >= 2
            && target >= minimumPairSum()
            && target <= maximumPairSum();
    }

    private SearchResult search(long target) {
        if (!canContainPairMatching(target)) {
            return SearchResult.notFound();
        }

        int left = 0;
        int right = values.length - 1;

        while (left < right) {
            long sum = sumAt(left, right);
            if (sum == target) {
                return SearchResult.match(left, right);
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return SearchResult.notFound();
    }

    private long minimumPairSum() {
        return (long) values[0] + values[1];
    }

    private long maximumPairSum() {
        int lastIndex = values.length - 1;
        return (long) values[lastIndex - 1] + values[lastIndex];
    }

    private long sumAt(int leftIndex, int rightIndex) {
        return (long) values[leftIndex] + values[rightIndex];
    }

    private static void validateSorted(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        for (int index = 1; index < values.length; index++) {
            if (values[index - 1] > values[index]) {
                throw new IllegalArgumentException(
                    "two-pointer search requires values sorted in nondecreasing order");
            }
        }
    }

    private static final class SearchResult {

        private static final SearchResult NOT_FOUND = new SearchResult(false, -1, -1);

        private final boolean found;
        private final int leftIndex;
        private final int rightIndex;

        private SearchResult(boolean found, int leftIndex, int rightIndex) {
            this.found = found;
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
        }

        private static SearchResult notFound() {
            return NOT_FOUND;
        }

        private static SearchResult match(int leftIndex, int rightIndex) {
            return new SearchResult(true, leftIndex, rightIndex);
        }

        private boolean found() {
            return found;
        }

        private PairMatch toPairMatch(int[] values) {
            return new PairMatch(leftIndex, rightIndex, values[leftIndex], values[rightIndex]);
        }
    }
}
