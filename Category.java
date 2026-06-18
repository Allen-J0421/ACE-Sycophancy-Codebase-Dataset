public interface Category<M> {
  <A, B, C> Morphism<A, C> compose(Morphism<B, C> f, Morphism<A, B> g);

  <A> Morphism<A, A> identity(Class<A> clazz);

  interface Morphism<A, B> {
    B apply(A a);
  }

  static <A> Category<A> setCategory() {
    return new Category<A>() {
      @Override
      public <A, B, C> Morphism<A, C> compose(Morphism<B, C> f, Morphism<A, B> g) {
        return a -> f.apply(g.apply(a));
      }

      @Override
      public <A> Morphism<A, A> identity(Class<A> clazz) {
        return a -> a;
      }
    };
  }
}
