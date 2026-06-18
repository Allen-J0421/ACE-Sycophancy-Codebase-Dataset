import java.util.Optional;

public final class TwoPointers {

    private TwoPointers() {
    }

    public static Optional<PairMatch> findPairWithSum(int[] sortedValues, int target) {
        validateSortedInput(sortedValues);
        return findPairInSortedValues(sortedValues, target);
    }

    public static boolean hasPairWithSum(int[] sortedValues, int target) {
        return findPairWithSum(sortedValues, target).isPresent();
    }

    private static Optional<PairMatch> findPairInSortedValues(int[] sortedValues, int target) {
        int left = 0;
        int right = sortedValues.length - 1;

        while (left < right) {
            long sum = (long) sortedValues[left] + sortedValues[right];

            if (sum == target) {
                return Optional.of(new PairMatch(
                    left,
                    right,
                    sortedValues[left],
                    sortedValues[right]));
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return Optional.empty();
    }

    private static void validateSortedInput(int[] sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }

        for (int index = 1; index < sortedValues.length; index++) {
            if (sortedValues[index - 1] > sortedValues[index]) {
                throw new IllegalArgumentException(
                    "two-pointer search requires values sorted in nondecreasing order");
            }
        }
    }

    public record PairMatch(int leftIndex, int rightIndex, int leftValue, int rightValue) {
        public long sum() {
            return (long) leftValue + rightValue;
        }
    }
}
