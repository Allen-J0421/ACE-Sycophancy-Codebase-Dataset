public class RateLimitMiddleware implements SolveMiddleware {
  private final RateLimiter limiter;

  public RateLimitMiddleware(int maxRequestsPerSecond) {
    this.limiter = new RateLimiter(maxRequestsPerSecond);
  }

  @Override
  public CoinChangeResult process(SolveContext context, SolveChain chain) {
    if (!limiter.allowRequest()) {
      throw new RuntimeException("Rate limit exceeded: " + limiter);
    }

    return chain.execute(context);
  }

  public RateLimiter getRateLimiter() {
    return limiter;
  }
}
