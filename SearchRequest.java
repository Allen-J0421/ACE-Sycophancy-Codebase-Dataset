class SearchRequest<T extends Comparable<? super T>> {
    private final T[] values;
    private final T target;

    private SearchRequest(T[] values, T target) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }

        this.values = values.clone();
        this.target = target;
    }

    static <T extends Comparable<? super T>> Builder<T> builder() {
        return new Builder<>();
    }

    T[] values() {
        return values.clone();
    }

    T target() {
        return target;
    }

    static class Builder<T extends Comparable<? super T>> {
        private T[] values;
        private T target;

        Builder<T> values(T[] values) {
            this.values = values;
            return this;
        }

        Builder<T> target(T target) {
            this.target = target;
            return this;
        }

        SearchRequest<T> build() {
            return new SearchRequest<>(values, target);
        }
    }
}
