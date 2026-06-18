public abstract class Traverse<A, B> {
  public abstract <F> F traverse(java.util.List<A> as,
                                   java.util.function.Function<B, F> effect);

  public static <A> Traverse<A, A> identity() {
    return new Traverse<A, A>() {
      @Override public <F> F traverse(java.util.List<A> as,
                                        java.util.function.Function<A, F> effect) {
        for (A a : as) {
          effect.apply(a);
        }
        return null;
      }
    };
  }

  public static Traverse<Integer, Integer> indexed() {
    return new Traverse<Integer, Integer>() {
      @Override public <F> F traverse(java.util.List<Integer> as,
                                        java.util.function.Function<Integer, F> effect) {
        for (int i = 0; i < as.size(); i++) {
          effect.apply(i);
        }
        return null;
      }
    };
  }

  public <C> Traverse<A, C> map(java.util.function.Function<B, C> f) {
    return new Traverse<A, C>() {
      @Override public <F> F traverse(java.util.List<A> as,
                                        java.util.function.Function<C, F> effect) {
        return Traverse.this.traverse(as, b -> effect.apply(f.apply(b)));
      }
    };
  }
}
