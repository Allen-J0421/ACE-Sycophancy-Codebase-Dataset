class TwoPointers {

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
        int[] sortedNumbers = {-3, -1, 0, 1, 2};
        int targetSum = -2;
        boolean pairExists = hasPairWithSum(sortedNumbers, targetSum);

        System.out.println(pairExists);
    }
}
