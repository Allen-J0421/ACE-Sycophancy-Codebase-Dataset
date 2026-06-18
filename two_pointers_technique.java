import java.util.Optional;

record PairResult(int left, int right) {}

class TwoPointers {

    // arr must be sorted ascending. Returns indices of the pair summing to target.
    static Optional<PairResult> findPairWithSum(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            int sum = arr[left] + arr[right];

            if (sum == target) {
                return Optional.of(new PairResult(left, right));
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
                .ifPresentOrElse(System.out::println, () -> System.out.println("No pair found"));
    }
}
