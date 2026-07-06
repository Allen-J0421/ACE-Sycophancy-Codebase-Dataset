package euclidean;

final class LoggingGcdProvider implements GcdProvider {

    private final GcdProvider delegate;
    private final GcdObserver observer;

    LoggingGcdProvider(GcdProvider delegate, GcdObserver observer) {
        this.delegate = delegate;
        this.observer = observer;
    }

    @Override
    public int compute(int a, int b) {
        int result = delegate.compute(a, b);
        observer.onCompute(a, b, result);
        return result;
    }
}
