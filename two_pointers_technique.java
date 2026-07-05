interface PairSearchStrategy {
    boolean hasPairWithSum(int[] sortedArr, int target);
}

class TwoPointerService implements PairSearchStrategy {

    @Override
    public boolean hasPairWithSum(int[] sortedArr, int target) {
        int left = 0, right = sortedArr.length - 1;

        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];

            if (sum == target)
                return true;
            else if (sum < target)
                left++;
            else
                right--;
        }

        return false;
    }
}

class TwoPointers {

    private final PairSearchStrategy strategy;

    TwoPointers(PairSearchStrategy strategy) {
        this.strategy = strategy;
    }

    boolean twoSum(int[] arr, int target) {
        return strategy.hasPairWithSum(arr, target);
    }

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        TwoPointers tp = new TwoPointers(new TwoPointerService());

        if (tp.twoSum(arr, target)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
