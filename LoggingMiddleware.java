public class LoggingMiddleware implements SolveMiddleware {
  private final boolean verbose;

  public LoggingMiddleware() {
    this(false);
  }

  public LoggingMiddleware(boolean verbose) {
    this.verbose = verbose;
  }

  @Override
  public CoinChangeResult process(SolveContext context, SolveChain chain) {
    if (verbose) {
      System.out.println("[MIDDLEWARE] Starting solve for " + context);
    }

    long startTime = System.nanoTime();
    CoinChangeResult result = chain.execute(context);
    long elapsed = System.nanoTime() - startTime;

    if (verbose) {
      System.out.println(String.format(
          "[MIDDLEWARE] Completed in %.2fms: %d ways",
          elapsed / 1_000_000.0, result.getWays()));
    }

    return result;
  }
}
