class ValidatingSearchAlgorithm<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    private final SearchAlgorithm<T> delegate;
    private final SearchRequestValidator<T> validator;

    ValidatingSearchAlgorithm(SearchAlgorithm<T> delegate, SearchRequestValidator<T> validator) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (validator == null) {
            throw new IllegalArgumentException("validator must not be null");
        }

        this.delegate = delegate;
        this.validator = validator;
    }

    @Override
    public SearchResult search(SearchRequest<T> request) {
        validator.validate(request);
        return delegate.search(request);
    }
}
