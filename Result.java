import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Result<T> {
    public static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    public static <T> Result<T> failure(String message, Throwable cause) {
        return new Failure<>(message, cause);
    }

    public abstract <U> Result<U> map(Function<T, U> fn);

    public abstract <U> Result<U> flatMap(Function<T, Result<U>> fn);

    public abstract void ifSuccess(Consumer<T> consumer);

    public abstract void ifFailure(Consumer<String> consumer);

    public abstract Optional<T> toOptional();

    public abstract T getOrThrow();

    public abstract T getOrNull();

    public abstract boolean isSuccess();

    public abstract String getErrorMessage();

    private static class Success<T> extends Result<T> {
        private final T value;

        Success(T value) {
            this.value = value;
        }

        @Override
        public <U> Result<U> map(Function<T, U> fn) {
            try {
                return Result.success(fn.apply(value));
            } catch (Exception e) {
                return Result.failure("Mapping failed", e);
            }
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> fn) {
            try {
                return fn.apply(value);
            } catch (Exception e) {
                return Result.failure("FlatMap failed", e);
            }
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public void ifFailure(Consumer<String> consumer) {
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public T getOrNull() {
            return value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public String getErrorMessage() {
            return null;
        }
    }

    private static class Failure<T> extends Result<T> {
        private final String message;
        private final Throwable cause;

        Failure(String message) {
            this(message, null);
        }

        Failure(String message, Throwable cause) {
            this.message = message;
            this.cause = cause;
        }

        @Override
        public <U> Result<U> map(Function<T, U> fn) {
            return Result.failure(message, cause);
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> fn) {
            return Result.failure(message, cause);
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
        }

        @Override
        public void ifFailure(Consumer<String> consumer) {
            consumer.accept(message);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public T getOrThrow() {
            if (cause != null) {
                throw new RuntimeException(message, cause);
            }
            throw new RuntimeException(message);
        }

        @Override
        public T getOrNull() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String getErrorMessage() {
            return message;
        }
    }
}
