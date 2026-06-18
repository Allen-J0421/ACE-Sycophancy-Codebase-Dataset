final class TwoPointers {

    private static final int SAMPLE_TARGET_SUM = -2;

    private TwoPointers() {
    }

    static boolean hasPairWithSum(int[] sortedNumbers, int targetSum) {
        int leftIndex = 0;
        int rightIndex = sortedNumbers.length - 1;

        while (leftIndex < rightIndex) {
            int sum = sortedNumbers[leftIndex] + sortedNumbers[rightIndex];

            if (sum == targetSum) {
                return true;
            }

            if (sum < targetSum) {
                leftIndex++;
            } else {
                rightIndex--;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        boolean pairExists = hasPairWithSum(sampleSortedNumbers(), SAMPLE_TARGET_SUM);

        System.out.println(pairExists);
    }

    private static int[] sampleSortedNumbers() {
        return new int[] {-3, -1, 0, 1, 2};
    }
}
