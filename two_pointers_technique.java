class TwoPointersTechnique {

    private TwoPointersTechnique() {
        // Utility class.
    }

    /**
     * Returns true when an array contains two values that add up to the target.
     * The input is not mutated; a sorted copy is used internally.
     */
    static boolean hasPairWithSum(int[] values, int target) {
        if (values == null || values.length < 2) {
            return false;
        }

        int[] sortedValues = values.clone();
        java.util.Arrays.sort(sortedValues);
        return hasPairWithSumSorted(sortedValues, target);
    }

    /**
     * Two-pointer search over an already sorted array.
     */
    static boolean hasPairWithSumSorted(int[] sortedValues, int target) {
        if (sortedValues == null || sortedValues.length < 2) {
            return false;
        }

        int left = 0;
        int right = sortedValues.length - 1;

        while (left < right) {
            int sum = sortedValues[left] + sortedValues[right];

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

    public static void main(String[] args) {
        int[] values = {2, -3, 1, 0, -1};
        int target = -2;

        System.out.println(hasPairWithSum(values, target));
    }
}
