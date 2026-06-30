class ResultHandlingSearchAlgorithm<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    private final SearchAlgorithm<T> delegate;
    private final ResultHandler resultHandler;

    ResultHandlingSearchAlgorithm(SearchAlgorithm<T> delegate, ResultHandler resultHandler) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (resultHandler == null) {
            throw new IllegalArgumentException("resultHandler must not be null");
        }

        this.delegate = delegate;
        this.resultHandler = resultHandler;
    }

    @Override
    public SearchResult search(SearchRequest<T> request) {
        SearchResult result = delegate.search(request);
        resultHandler.handle(result);
        return result;
    }
}
