public abstract class Lens<S, T> {
  public abstract T get(S s);
  public abstract S set(T t, S s);

  public S modify(java.util.function.Function<T, T> f, S s) {
    return set(f.apply(get(s)), s);
  }

  public <U> Lens<S, U> compose(Lens<T, U> other) {
    return new Lens<S, U>() {
      @Override public U get(S s) {
        return other.get(Lens.this.get(s));
      }
      @Override public S set(U u, S s) {
        T t = Lens.this.get(s);
        return Lens.this.set(other.set(u, t), s);
      }
    };
  }

  public static <S> Lens<S, S> identity() {
    return new Lens<S, S>() {
      @Override public S get(S s) { return s; }
      @Override public S set(S s, S ignored) { return s; }
    };
  }
}
