package euclidean;

sealed interface Result<T, E> permits Result.Success, Result.Failure {
    record Success<T, E>(T value) implements Result<T, E> {}
    record Failure<T, E>(E error) implements Result<T, E> {}

    static <T, E> Result<T, E> success(T value) { return new Success<>(value); }
    static <T, E> Result<T, E> failure(E error) { return new Failure<>(error); }

    @SuppressWarnings("unchecked")
    default <U> Result<U, E> map(ResultMapper<T, U> mapper) {
        if (this instanceof Result.Success<?, ?> s) {
            return Result.success(mapper.apply((T) s.value()));
        }
        return (Result<U, E>) this;
    }

    @SuppressWarnings("unchecked")
    default <F> Result<T, F> mapError(ResultMapper<E, F> mapper) {
        if (this instanceof Result.Failure<?, ?> f) {
            return Result.failure(mapper.apply((E) f.error()));
        }
        return (Result<T, F>) this;
    }

    @SuppressWarnings("unchecked")
    default <R> R fold(ResultMapper<T, R> onSuccess, ResultMapper<E, R> onFailure) {
        if (this instanceof Result.Success<?, ?> s) {
            return onSuccess.apply((T) s.value());
        }
        return onFailure.apply((E) ((Result.Failure<?, ?>) this).error());
    }
}
