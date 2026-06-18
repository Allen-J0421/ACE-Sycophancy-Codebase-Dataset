class TwoPointers {

    static boolean hasPairWithSum(int[] sortedNumbers, int target) {
        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left < right) {
            int sum = sortedNumbers[left] + sortedNumbers[right];

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
        int[] sortedNumbers = {-3, -1, 0, 1, 2};
        int target = -2;

        if (hasPairWithSum(sortedNumbers, target)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
