public interface Applicative<A, B> extends Functor<A, B> {
  <C> Applicative<A, C> apply(Applicative<A, java.util.function.Function<B, C>> fab);

  @Override default <C> Applicative<A, C> map(java.util.function.Function<B, C> f) {
    return apply(pure(f));
  }

  static <A, B> Applicative<A, B> pure(B b) {
    return new Applicative<A, B>() {
      @Override public <C> Applicative<A, C> apply(Applicative<A, java.util.function.Function<B, C>> fab) {
        return new Applicative<A, C>() {
          @Override public <D> Applicative<A, D> apply(Applicative<A, java.util.function.Function<C, D>> fab2) {
            return pure(null);
          }
        };
      }
    };
  }
}
