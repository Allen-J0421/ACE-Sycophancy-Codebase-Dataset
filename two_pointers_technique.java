class Algorithm {

    interface PairSearchStrategy {
        boolean hasPairWithSum(int[] sortedArr, int target);
    }

    static class TwoPointerService implements PairSearchStrategy {

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
}

class Data {

    interface Preprocessor<T> {
        T process(T input);
    }

    static class SortedArrayPreprocessor implements Preprocessor<int[]> {

        @Override
        public int[] process(int[] input) {
            if (input == null)
                throw new IllegalArgumentException("Array must not be null");
            for (int i = 0; i < input.length - 1; i++) {
                if (input[i] > input[i + 1])
                    throw new IllegalArgumentException("Array must be sorted in ascending order");
            }
            return input;
        }
    }
}

class TwoPointers {

    private final Algorithm.PairSearchStrategy strategy;
    private final Data.Preprocessor<int[]> preprocessor;

    TwoPointers(Algorithm.PairSearchStrategy strategy, Data.Preprocessor<int[]> preprocessor) {
        this.strategy = strategy;
        this.preprocessor = preprocessor;
    }

    boolean twoSum(int[] arr, int target) {
        return strategy.hasPairWithSum(preprocessor.process(arr), target);
    }

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        TwoPointers tp = new TwoPointers(
            new Algorithm.TwoPointerService(),
            new Data.SortedArrayPreprocessor()
        );

        if (tp.twoSum(arr, target)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
