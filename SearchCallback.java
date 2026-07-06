abstract class SearchCallback<T> {
    void onFound(T key) {}
    void onNotFound(T key) {}

    static <T> SearchCallback<T> noOp() {
        return new SearchCallback<T>() {};
    }

    final SearchCallback<T> andThen(SearchCallback<T> next) {
        SearchCallback<T> self = this;
        return new SearchCallback<T>() {
            @Override void onFound(T key) { self.onFound(key); next.onFound(key); }
            @Override void onNotFound(T key) { self.onNotFound(key); next.onNotFound(key); }
        };
    }
}
