public abstract class Fold<A, B> {
  public abstract B fold(java.util.List<A> as);

  public static <A> Fold<A, Integer> length() {
    return new Fold<A, Integer>() {
      @Override public Integer fold(java.util.List<A> as) { return as.size(); }
    };
  }

  public static Fold<Integer, Integer> sum() {
    return new Fold<Integer, Integer>() {
      @Override public Integer fold(java.util.List<Integer> as) {
        return as.stream().mapToInt(Integer::intValue).sum();
      }
    };
  }

  public static Fold<Integer, Integer> product() {
    return new Fold<Integer, Integer>() {
      @Override public Integer fold(java.util.List<Integer> as) {
        int result = 1;
        for (int a : as) result *= a;
        return result;
      }
    };
  }

  public static <A extends Comparable<A>> Fold<A, A> maximum() {
    return new Fold<A, A>() {
      @Override public A fold(java.util.List<A> as) {
        if (as.isEmpty()) throw new IllegalArgumentException("Empty list");
        return as.stream().max(A::compareTo).get();
      }
    };
  }

  public static <A extends Comparable<A>> Fold<A, A> minimum() {
    return new Fold<A, A>() {
      @Override public A fold(java.util.List<A> as) {
        if (as.isEmpty()) throw new IllegalArgumentException("Empty list");
        return as.stream().min(A::compareTo).get();
      }
    };
  }

  public <C> Fold<A, C> map(java.util.function.Function<B, C> f) {
    return new Fold<A, C>() {
      @Override public C fold(java.util.List<A> as) {
        return f.apply(Fold.this.fold(as));
      }
    };
  }
}
