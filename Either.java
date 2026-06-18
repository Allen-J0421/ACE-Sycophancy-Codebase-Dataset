public abstract class Either<L, R> {
  public abstract <T> T fold(java.util.function.Function<L, T> leftFn,
                              java.util.function.Function<R, T> rightFn);

  public <T> Either<L, T> map(java.util.function.Function<R, T> f) {
    return fold(
        left -> new Left<>(left),
        right -> new Right<>(f.apply(right))
    );
  }

  public <T> Either<T, R> mapLeft(java.util.function.Function<L, T> f) {
    return fold(
        left -> new Left<>(f.apply(left)),
        right -> new Right<>(right)
    );
  }

  public <T> Either<L, T> flatMap(java.util.function.Function<R, Either<L, T>> f) {
    return fold(
        left -> new Left<>(left),
        right -> f.apply(right)
    );
  }

  public static <L, R> Either<L, R> left(L value) {
    return new Left<>(value);
  }

  public static <L, R> Either<L, R> right(R value) {
    return new Right<>(value);
  }

  private static class Left<L, R> extends Either<L, R> {
    private final L value;
    Left(L value) { this.value = value; }
    @Override public <T> T fold(java.util.function.Function<L, T> leftFn,
                                  java.util.function.Function<R, T> rightFn) {
      return leftFn.apply(value);
    }
  }

  private static class Right<L, R> extends Either<L, R> {
    private final R value;
    Right(R value) { this.value = value; }
    @Override public <T> T fold(java.util.function.Function<L, T> leftFn,
                                  java.util.function.Function<R, T> rightFn) {
      return rightFn.apply(value);
    }
  }
}
