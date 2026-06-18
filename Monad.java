public interface Monad<M, A> {
  <B> Monad<M, B> bind(java.util.function.Function<A, Monad<M, B>> f);

  default <B> Monad<M, B> flatMap(java.util.function.Function<A, Monad<M, B>> f) {
    return bind(f);
  }

  static <M, A> Monad<M, A> pure(A a) {
    return new Monad<M, A>() {
      @Override public <B> Monad<M, B> bind(java.util.function.Function<A, Monad<M, B>> f) {
        return f.apply(a);
      }
    };
  }
}
