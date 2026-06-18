public final class TwoPointers {

    private TwoPointers() {
    }

    public static boolean hasPairWithSum(int[] sortedValues, int target) {
        validateSortedInput(sortedValues);

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

    public static void main(String[] args) {
        int[] sortedValues = {-3, -1, 0, 1, 2};
        int target = -2;

        System.out.println(hasPairWithSum(sortedValues, target));
    }
}
