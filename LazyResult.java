public class LazyResult {
  private final java.util.function.Supplier<CoinChangeResult> supplier;
  private CoinChangeResult cached;
  private boolean evaluated = false;

  public LazyResult(java.util.function.Supplier<CoinChangeResult> supplier) {
    this.supplier = supplier;
  }

  public CoinChangeResult get() {
    if (!evaluated) {
      cached = supplier.get();
      evaluated = true;
    }
    return cached;
  }

  public boolean isEvaluated() {
    return evaluated;
  }

  public int getWays() {
    return get().getWays();
  }

  @Override
  public String toString() {
    return "LazyResult{evaluated=" + evaluated + "}";
  }
}
