final class TwoPointers {

    private static final int SAMPLE_TARGET_SUM = -2;

    private TwoPointers() {
    }

    static boolean hasPairWithSum(int[] sortedNumbers, int targetSum) {
        int leftIndex = 0;
        int rightIndex = sortedNumbers.length - 1;

        while (leftIndex < rightIndex) {
            int currentSum = pairSum(sortedNumbers, leftIndex, rightIndex);

            if (currentSum == targetSum) {
                return true;
            }

            if (currentSum < targetSum) {
                leftIndex++;
                continue;
            }

            rightIndex--;
        }

        return false;
    }

    private static int pairSum(int[] numbers, int leftIndex, int rightIndex) {
        return numbers[leftIndex] + numbers[rightIndex];
    }

    public static void main(String[] args) {
        System.out.println(samplePairExists());
    }

    private static boolean samplePairExists() {
        return hasPairWithSum(sampleSortedNumbers(), SAMPLE_TARGET_SUM);
    }

    private static int[] sampleSortedNumbers() {
        return new int[] {-3, -1, 0, 1, 2};
    }
}
