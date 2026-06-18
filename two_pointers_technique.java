import java.util.Arrays;
import java.util.Optional;

class TwoPointers {

    // arr must be sorted ascending. Returns indices [left, right] of the pair summing to target.
    static Optional<int[]> findPairWithSum(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            int sum = arr[left] + arr[right];

            if (sum == target) {
                return Optional.of(new int[]{left, right});
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return Optional.empty();
    }
}

class TwoPointersDemo {

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        TwoPointers.findPairWithSum(arr, target)
                .map(Arrays::toString)
                .ifPresentOrElse(System.out::println, () -> System.out.println("No pair found"));
    }
}
