class TwoPointersTechnique {

    private TwoPointersTechnique() {
        // Utility class.
    }

    /**
     * Returns true when a sorted array contains two values that add up to the target.
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
        int[] sortedValues = {-3, -1, 0, 1, 2};
        int target = -2;

        System.out.println(hasPairWithSumSorted(sortedValues, target));
    }
}
