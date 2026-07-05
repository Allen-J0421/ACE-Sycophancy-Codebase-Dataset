import java.util.function.Function;
import java.util.function.Supplier;

class Algorithm {

    interface ExecutionContext {
        void onSearchStart(int[] arr, int target);
        void onSearchComplete(Data.SearchResult result, long elapsedNanos);
    }

    static class NoOpContext implements ExecutionContext {
        @Override public void onSearchStart(int[] arr, int target) {}
        @Override public void onSearchComplete(Data.SearchResult result, long elapsedNanos) {}
    }

    static class LoggingContext implements ExecutionContext {

        @Override
        public void onSearchStart(int[] arr, int target) {
            System.out.println("[LOG] Search started: target=" + target + ", length=" + arr.length);
        }

        @Override
        public void onSearchComplete(Data.SearchResult result, long elapsedNanos) {
            System.out.println("[LOG] Search completed in " + elapsedNanos + "ns: " + result.getStatus());
        }
    }

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

    static class InstrumentedStrategy implements PairSearchStrategy {

        private final PairSearchStrategy delegate;
        private final ExecutionContext context;

        InstrumentedStrategy(PairSearchStrategy delegate, ExecutionContext context) {
            this.delegate = delegate;
            this.context = context;
        }

        @Override
        public Data.SearchResult hasPairWithSum(int[] sortedArr, int target) {
            context.onSearchStart(sortedArr, target);
            long start = System.nanoTime();
            Data.SearchResult result = delegate.hasPairWithSum(sortedArr, target);
            context.onSearchComplete(result, System.nanoTime() - start);
            return result;
        }
    }
}

class Data {

    interface Preprocessor<T> {
        T process(T input);

        default Preprocessor<T> andThen(Preprocessor<T> next) {
            return input -> next.process(this.process(input));
        }
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

    static class MinLengthPreprocessor implements Preprocessor<int[]> {

        private final int minLength;

        MinLengthPreprocessor(int minLength) {
            this.minLength = minLength;
        }

        @Override
        public int[] process(int[] input) {
            if (input.length < minLength)
                throw new IllegalArgumentException(
                    "Array must have at least " + minLength + " elements, got " + input.length);
            return input;
        }
    }

    static class Pipeline<T> {

        private Preprocessor<T> chain;

        private Pipeline() {
            this.chain = input -> input;
        }

        static <T> Pipeline<T> start() {
            return new Pipeline<>();
        }

        Pipeline<T> add(Preprocessor<T> preprocessor) {
            this.chain = chain.andThen(preprocessor);
            return this;
        }

        Preprocessor<T> build() {
            return chain;
        }
    }

    static abstract class SearchResult {

        abstract String getStatus();
        abstract <R> R fold(Function<Found, R> onFound, Supplier<R> onNotFound);

        static SearchResult found(int leftIndex, int rightIndex) {
            return new Found(leftIndex, rightIndex);
        }

        static SearchResult notFound() {
            return new NotFound();
        }

        static class Found extends SearchResult {

            private final int leftIndex;
            private final int rightIndex;

            Found(int leftIndex, int rightIndex) {
                this.leftIndex = leftIndex;
                this.rightIndex = rightIndex;
            }

            int getLeftIndex()  { return leftIndex; }
            int getRightIndex() { return rightIndex; }

            @Override
            String getStatus() {
                return "Pair found at indices [" + leftIndex + ", " + rightIndex + "]";
            }

            @Override
            <R> R fold(Function<Found, R> onFound, Supplier<R> onNotFound) {
                return onFound.apply(this);
            }
        }

        static class NotFound extends SearchResult {

            @Override
            String getStatus() { return "No pair found"; }

            @Override
            <R> R fold(Function<Found, R> onFound, Supplier<R> onNotFound) {
                return onNotFound.get();
            }
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

    Data.SearchResult twoSum(int[] arr, int target) {
        return strategy.hasPairWithSum(preprocessor.process(arr), target);
    }

    public static void main(String[] args) {
        int[] arr = {-3, -1, 0, 1, 2};
        int target = -2;

        Data.Preprocessor<int[]> preprocessor = Data.Pipeline.<int[]>start()
            .add(new Data.SortedArrayPreprocessor())
            .add(new Data.MinLengthPreprocessor(2))
            .build();

        TwoPointers tp = new TwoPointers(
            new Algorithm.InstrumentedStrategy(
                new Algorithm.TwoPointerService(),
                new Algorithm.LoggingContext()
            ),
            preprocessor
        );

        String output = tp.twoSum(arr, target).fold(
            found -> found.getStatus() + "\nValues: arr[" + found.getLeftIndex() + "] + arr[" + found.getRightIndex() + "]",
            () -> "No pair found"
        );

        System.out.println(output);
    }
}
