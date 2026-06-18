// Two Pointers Algorithm - finds if two elements in a sorted array sum to target
class TwoPointersTechnique {

    // Finds if two elements in a sorted array sum to the target value.
    // Time: O(n), Space: O(1)
    static boolean findPairSum(int[] sortedArray, int targetSum) {
        if (sortedArray == null || sortedArray.length < 2) {
            return false;
        }

        int left = 0;
        int right = sortedArray.length - 1;

        while (left < right) {
            int currentSum = sortedArray[left] + sortedArray[right];

            if (currentSum == targetSum) {
                return true;
            } else if (currentSum < targetSum) {
                left++;
            } else {
                right--;
            }
        }

        return false;
    }
}

class TwoPointersTechniqueDemo {
    public static void main(String[] args) {
        int[] sortedArray = {-3, -1, 0, 1, 2};
        int targetSum = -2;

        boolean found = TwoPointersTechnique.findPairSum(sortedArray, targetSum);
        System.out.println(found);
    }
}
