public abstract class Result<T> {
  public static final class Success<T> extends Result<T> {
    private final T value;

    public Success(T value) {
      this.value = value;
    }

    public T getValue() {
      return value;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public String toString() {
      return String.format("Success(%s)", value);
    }
  }

  public static final class Failure<T> extends Result<T> {
    private final Exception error;
    private final String message;

    public Failure(String message) {
      this.error = null;
      this.message = message;
    }

    public Failure(Exception error) {
      this.error = error;
      this.message = error.getMessage();
    }

    public Exception getError() {
      return error;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public String toString() {
      return String.format("Failure(%s)", message);
    }
  }

  public abstract boolean isSuccess();

  public T getOrElse(T defaultValue) {
    if (this instanceof Success) {
      return ((Success<T>) this).getValue();
    }
    return defaultValue;
  }

  public <U> Result<U> map(java.util.function.Function<T, U> f) {
    if (this instanceof Success) {
      try {
        return new Success<>(f.apply(((Success<T>) this).getValue()));
      } catch (Exception e) {
        return new Failure<>(e);
      }
    }
    return new Failure<>(((Failure<T>) this).getMessage());
  }
}
