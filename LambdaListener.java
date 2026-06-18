import java.util.function.Consumer;

/**
 * Listener that accepts lambda expressions for event handling.
 */
public class LambdaListener extends AbstractListener {
    private final Consumer<Integer> onStart;
    private final Consumer<PrefixSum.PrefixSumResult> onComplete;
    private final Consumer<Exception> onError;

    public LambdaListener(Consumer<Integer> onStart,
                         Consumer<PrefixSum.PrefixSumResult> onComplete,
                         Consumer<Exception> onError) {
        this.onStart = onStart;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    @Override
    protected void handleComputationStart(int arraySize) {
        if (onStart != null) {
            onStart.accept(arraySize);
        }
    }

    @Override
    protected void handleComputationComplete(PrefixSum.PrefixSumResult result) {
        if (onComplete != null) {
            onComplete.accept(result);
        }
    }

    @Override
    protected void handleComputationError(Exception exception) {
        if (onError != null) {
            onError.accept(exception);
        }
    }

    public static class Builder {
        private Consumer<Integer> onStart;
        private Consumer<PrefixSum.PrefixSumResult> onComplete;
        private Consumer<Exception> onError;

        public Builder onStart(Consumer<Integer> handler) {
            this.onStart = handler;
            return this;
        }

        public Builder onComplete(Consumer<PrefixSum.PrefixSumResult> handler) {
            this.onComplete = handler;
            return this;
        }

        public Builder onError(Consumer<Exception> handler) {
            this.onError = handler;
            return this;
        }

        public LambdaListener build() {
            return new LambdaListener(onStart, onComplete, onError);
        }
    }
}
