import java.util.HashSet;
import java.util.Set;

final class TwoPointersTechnique {

    private TwoPointersTechnique() {
        // Utility class.
    }

    /**
     * Returns true when an array contains two values that add up to the target.
     * This version works on unsorted input in linear time and does not mutate the caller's array.
     */
    static boolean hasPairWithSum(int[] values, int target) {
        if (!hasAtLeastTwoValues(values)) {
            return false;
        }

        Set<Integer> seenValues = new HashSet<>(values.length);

        for (int value : values) {
            long complement = (long) target - value;
            if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE
                && seenValues.contains((int) complement)) {
                return true;
            }
            seenValues.add(value);
        }

        return false;
    }

    /**
     * Two-pointer search over an already sorted array.
     */
    static boolean hasPairWithSumSorted(int[] sortedValues, int target) {
        if (!hasAtLeastTwoValues(sortedValues)) {
            return false;
        }

        int left = 0;
        int right = sortedValues.length - 1;

        while (left < right) {
            long sum = (long) sortedValues[left] + sortedValues[right];

            if (sum == target) {
                return true;
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return false;
    }

    private static boolean hasAtLeastTwoValues(int[] values) {
        return values != null && values.length >= 2;
    }
}
