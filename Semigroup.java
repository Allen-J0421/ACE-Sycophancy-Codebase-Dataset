public abstract class Semigroup<A> {
  public abstract A combine(A a, A b);

  public A combineAll(java.util.List<A> as) {
    if (as.isEmpty()) throw new IllegalArgumentException("Semigroup requires non-empty list");
    A result = as.get(0);
    for (int i = 1; i < as.size(); i++) {
      result = combine(result, as.get(i));
    }
    return result;
  }

  public static Semigroup<Integer> intMax() {
    return new Semigroup<Integer>() {
      @Override public Integer combine(Integer a, Integer b) { return Math.max(a, b); }
    };
  }

  public static Semigroup<Integer> intMin() {
    return new Semigroup<Integer>() {
      @Override public Integer combine(Integer a, Integer b) { return Math.min(a, b); }
    };
  }

  public static <A> Semigroup<java.util.List<A>> listAppend() {
    return new Semigroup<java.util.List<A>>() {
      @Override public java.util.List<A> combine(java.util.List<A> a, java.util.List<A> b) {
        java.util.List<A> result = new java.util.ArrayList<>(a);
        result.addAll(b);
        return result;
      }
    };
  }
}
