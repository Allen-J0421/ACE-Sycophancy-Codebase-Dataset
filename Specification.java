public abstract class Specification<T> {
  public abstract boolean isSatisfiedBy(T candidate);

  public abstract String getDescription();

  public Specification<T> and(Specification<T> other) {
    return new CompositeSpecification<T>(this, other, "AND");
  }

  public Specification<T> or(Specification<T> other) {
    return new CompositeSpecification<T>(this, other, "OR");
  }

  public Specification<T> not() {
    return new NegatedSpecification<T>(this);
  }

  private static class CompositeSpecification<T> extends Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;
    private final String operator;

    CompositeSpecification(Specification<T> left, Specification<T> right, String operator) {
      this.left = left;
      this.right = right;
      this.operator = operator;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
      if ("AND".equals(operator)) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
      } else {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
      }
    }

    @Override
    public String getDescription() {
      return "(" + left.getDescription() + " " + operator + " " + right.getDescription() + ")";
    }
  }

  private static class NegatedSpecification<T> extends Specification<T> {
    private final Specification<T> spec;

    NegatedSpecification(Specification<T> spec) {
      this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
      return !spec.isSatisfiedBy(candidate);
    }

    @Override
    public String getDescription() {
      return "NOT(" + spec.getDescription() + ")";
    }
  }
}
