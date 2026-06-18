public abstract class Monoid<A> {
  public abstract A combine(A a, A b);
  public abstract A identity();

  public A fold(java.util.List<A> as) {
    A result = identity();
    for (A a : as) {
      result = combine(result, a);
    }
    return result;
  }

  public static Monoid<Integer> intAddition() {
    return new Monoid<Integer>() {
      @Override public Integer combine(Integer a, Integer b) { return a + b; }
      @Override public Integer identity() { return 0; }
    };
  }

  public static Monoid<Integer> intMultiplication() {
    return new Monoid<Integer>() {
      @Override public Integer combine(Integer a, Integer b) { return a * b; }
      @Override public Integer identity() { return 1; }
    };
  }

  public static <A> Monoid<java.util.List<A>> listConcat() {
    return new Monoid<java.util.List<A>>() {
      @Override public java.util.List<A> combine(java.util.List<A> a, java.util.List<A> b) {
        java.util.List<A> result = new java.util.ArrayList<>(a);
        result.addAll(b);
        return result;
      }
      @Override public java.util.List<A> identity() { return new java.util.ArrayList<>(); }
    };
  }
}
