class SearchRequest<T extends Comparable<? super T>> {
    private final T[] values;
    private final T target;

    SearchRequest(T[] values, T target) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }

        this.values = values.clone();
        this.target = target;
    }

    T[] values() {
        return values.clone();
    }

    T target() {
        return target;
    }
}
