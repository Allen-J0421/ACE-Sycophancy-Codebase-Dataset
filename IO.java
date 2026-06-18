public abstract class IO<A> {
  public abstract A unsafePerformIO();

  public <B> IO<B> bind(java.util.function.Function<A, IO<B>> f) {
    return new IO<B>() {
      @Override public B unsafePerformIO() {
        return f.apply(IO.this.unsafePerformIO()).unsafePerformIO();
      }
    };
  }

  public <B> IO<B> map(java.util.function.Function<A, B> f) {
    return bind(a -> pure(f.apply(a)));
  }

  public <B> IO<B> then(IO<B> next) {
    return bind(a -> next);
  }

  public static <A> IO<A> pure(A a) {
    return new IO<A>() {
      @Override public A unsafePerformIO() { return a; }
    };
  }

  public static IO<Void> println(String s) {
    return new IO<Void>() {
      @Override public Void unsafePerformIO() {
        System.out.println(s);
        return null;
      }
    };
  }

  public static <A> IO<A> sequence(java.util.List<IO<A>> ios) {
    return ios.isEmpty()
        ? pure(null)
        : ios.get(0).bind(a -> sequence(ios.subList(1, ios.size())));
  }
}
