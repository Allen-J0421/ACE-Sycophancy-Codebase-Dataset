class DefaultSearchRequestValidator<T extends Comparable<? super T>> implements SearchRequestValidator<T> {
    @Override
    public void validate(SearchRequest<T> request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        T[] values = request.values();
        for (T value : values) {
            if (value == null) {
                throw new IllegalArgumentException("values must not contain null elements");
            }
        }
    }
}
