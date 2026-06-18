public interface Functor<F, A> {
  <B> Functor<F, B> map(java.util.function.Function<A, B> f);

  static <F, A> Functor<F, A> pure(A a) {
    return new Functor<F, A>() {
      @Override public <B> Functor<F, B> map(java.util.function.Function<A, B> f) {
        return pure(f.apply(a));
      }
    };
  }
}
