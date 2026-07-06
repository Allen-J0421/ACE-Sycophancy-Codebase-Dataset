class Result<T> {
    private final T value;
    private final boolean success;

    private Result(T value, boolean success) {
        this.value = value;
        this.success = success;
    }

    static <T> Result<T> success(T value) {
        return new Result<>(value, true);
    }

    static <T> Result<T> failure() {
        return new Result<>(null, false);
    }

    void accept(ResultVisitor<T> visitor) {
        if (success) {
            visitor.onSuccess(value);
        } else {
            visitor.onFailure();
        }
    }
}
