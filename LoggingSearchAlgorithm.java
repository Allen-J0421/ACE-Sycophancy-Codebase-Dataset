import java.util.logging.Logger;

class LoggingSearchAlgorithm<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    private static final Logger LOGGER = Logger.getLogger(LoggingSearchAlgorithm.class.getName());

    private final SearchAlgorithm<T> delegate;

    LoggingSearchAlgorithm(SearchAlgorithm<T> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }

        this.delegate = delegate;
    }

    @Override
    public SearchResult search(SearchRequest<T> request) {
        long startNanos = System.nanoTime();
        try {
            SearchResult result = delegate.search(request);
            logSuccess(result, System.nanoTime() - startNanos);
            return result;
        } catch (RuntimeException exception) {
            logFailure(exception, System.nanoTime() - startNanos);
            throw exception;
        }
    }

    private void logSuccess(SearchResult result, long elapsedNanos) {
        LOGGER.info("Search completed using " + delegateName()
                + " in " + elapsedNanos + " ns"
                + ", found=" + result.isFound()
                + ", index=" + result.index());
    }

    private void logFailure(RuntimeException exception, long elapsedNanos) {
        LOGGER.warning("Search failed using " + delegateName()
                + " in " + elapsedNanos + " ns"
                + ": " + exception.getMessage());
    }

    private String delegateName() {
        return delegate.getClass().getSimpleName();
    }
}
