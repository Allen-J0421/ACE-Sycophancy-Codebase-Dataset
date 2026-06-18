final class TwoPointers {

    private static final int[] SAMPLE_SORTED_NUMBERS = {-3, -1, 0, 1, 2};
    private static final int SAMPLE_TARGET_SUM = -2;

    private TwoPointers() {
    }

    static boolean hasPairWithSum(int[] sortedNumbers, int targetSum) {
        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left < right) {
            int sum = sortedNumbers[left] + sortedNumbers[right];

            if (sum == targetSum) {
                return true;
            }

            if (sum < targetSum) {
                left++;
            } else {
                right--;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        boolean pairExists = hasPairWithSum(SAMPLE_SORTED_NUMBERS, SAMPLE_TARGET_SUM);

        System.out.println(pairExists);
    }
}
