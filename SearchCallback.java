interface SearchCallback<T> {
    void onFound(T key);
    void onNotFound(T key);

    static <T> SearchCallback<T> noOp() {
        return new SearchCallback<T>() {
            public void onFound(T key) {}
            public void onNotFound(T key) {}
        };
    }
}
