public class Result<T> {
    private final T value;
    private final Exception error;
    private final boolean success;

    private Result(T value, Exception error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> error(Exception error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    public T getValue() {
        if (!success) {
            throw new IllegalStateException("Cannot get value from error result", error);
        }
        return value;
    }

    public Exception getError() {
        if (success) {
            throw new IllegalStateException("Cannot get error from success result");
        }
        return error;
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (!success) {
            return Result.error(error);
        }
        try {
            return Result.ok(mapper.apply(value));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<T> mapError(ExceptionFunction mapper) {
        if (success) {
            return this;
        }
        return Result.error(mapper.apply(error));
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Ok(%s)", value);
        } else {
            return String.format("Error(%s)", error.getMessage());
        }
    }

    public interface Function<T, U> {
        U apply(T t) throws Exception;
    }

    public interface ExceptionFunction {
        Exception apply(Exception e);
    }
}
