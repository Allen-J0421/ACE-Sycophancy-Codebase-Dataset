import java.util.Arrays;

class TwoPointers {

    // Returns indices of the two elements that sum to target, or null if none found.
    static int[] twoSum(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            int sum = arr[left] + arr[right];

            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return null;
    }
}

class TwoPointersDemo {

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        int[] result = TwoPointers.twoSum(arr, target);
        System.out.println(result != null ? Arrays.toString(result) : "No pair found");
    }
}
