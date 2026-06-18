public abstract class Arrow<A, B> {
  public abstract B apply(A a);

  public <C> Arrow<A, C> andThen(Arrow<B, C> next) {
    return new Arrow<A, C>() {
      @Override public C apply(A a) {
        return next.apply(Arrow.this.apply(a));
      }
    };
  }

  public <Z> Arrow<Z, B> compose(Arrow<Z, A> prev) {
    return new Arrow<Z, B>() {
      @Override public B apply(Z z) {
        return Arrow.this.apply(prev.apply(z));
      }
    };
  }

  public Arrow<A, A> identity() {
    return new Arrow<A, A>() {
      @Override public A apply(A a) { return a; }
    };
  }

  public <C> Arrow<java.util.function.Function<A, B>, java.util.function.Function<A, C>>
      map(Arrow<B, C> arrow) {
    return new Arrow<java.util.function.Function<A, B>, java.util.function.Function<A, C>>() {
      @Override public java.util.function.Function<A, C> apply(java.util.function.Function<A, B> f) {
        return a -> arrow.apply(f.apply(a));
      }
    };
  }
}
