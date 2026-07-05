class Algorithm {

    interface PairSearchStrategy {
        Data.SearchResult hasPairWithSum(int[] sortedArr, int target);
    }

    static class TwoPointerService implements PairSearchStrategy {

        @Override
        public Data.SearchResult hasPairWithSum(int[] sortedArr, int target) {
            int left = 0, right = sortedArr.length - 1;

            while (left < right) {
                int sum = sortedArr[left] + sortedArr[right];

                if (sum == target)
                    return Data.SearchResult.found(left, right);
                else if (sum < target)
                    left++;
                else
                    right--;
            }

            return Data.SearchResult.notFound();
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

    static class SearchResult {

        private final boolean found;
        private final int leftIndex;
        private final int rightIndex;
        private final String status;

        private SearchResult(boolean found, int leftIndex, int rightIndex, String status) {
            this.found = found;
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
            this.status = status;
        }

        static SearchResult found(int leftIndex, int rightIndex) {
            return new SearchResult(true, leftIndex, rightIndex,
                "Pair found at indices [" + leftIndex + ", " + rightIndex + "]");
        }

        static SearchResult notFound() {
            return new SearchResult(false, -1, -1, "No pair found");
        }

        boolean isFound()    { return found; }
        int getLeftIndex()   { return leftIndex; }
        int getRightIndex()  { return rightIndex; }
        String getStatus()   { return status; }
    }
}

class TwoPointers {

    private final Algorithm.PairSearchStrategy strategy;
    private final Data.Preprocessor<int[]> preprocessor;

    TwoPointers(Algorithm.PairSearchStrategy strategy, Data.Preprocessor<int[]> preprocessor) {
        this.strategy = strategy;
        this.preprocessor = preprocessor;
    }

    Data.SearchResult twoSum(int[] arr, int target) {
        return strategy.hasPairWithSum(preprocessor.process(arr), target);
    }

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        TwoPointers tp = new TwoPointers(
            new Algorithm.TwoPointerService(),
            new Data.SortedArrayPreprocessor()
        );

        Data.SearchResult result = tp.twoSum(arr, target);
        System.out.println(result.getStatus());
    }
}
