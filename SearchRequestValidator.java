interface SearchRequestValidator<T extends Comparable<? super T>> {
    void validate(SearchRequest<T> request);
}
