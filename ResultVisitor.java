interface ResultVisitor<T> {
    void onSuccess(T value);
    void onFailure();
}
