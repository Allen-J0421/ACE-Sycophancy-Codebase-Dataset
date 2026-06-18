public class EnterpriseExample {

  public static void main(String[] args) {
    System.out.println("=== Enterprise Features Showcase ===\n");

    example1_ContextTracking();
    example2_MiddlewarePipeline();
    example3_LazyEvaluation();
    example4_ResultAggregation();
    example5_CacheAnalytics();
  }

  private static void example1_ContextTracking() {
    System.out.println("Example 1: Request Context Tracking");
    System.out.println("Problem: Track requests with metadata\n");

    SolveContext context = new SolveContext("REQ-12345", new int[]{1, 5, 10, 25}, 50);
    context.setAttribute("user_id", "user_001");
    context.setAttribute("priority", "high");

    System.out.println("Context: " + context);
    System.out.println("Metadata: " + context.getMetadata());
    System.out.println();
  }

  private static void example2_MiddlewarePipeline() {
    System.out.println("Example 2: Middleware Pipeline");
    System.out.println("Problem: Apply multiple middleware layers\n");

    MiddlewarePipeline pipeline = new MiddlewarePipeline(ctx -> {
      return CoinChange.solve(ctx.getCoins(), ctx.getTargetSum());
    });

    pipeline.use(new LoggingMiddleware(true));
    pipeline.use(new CacheMiddleware());

    SolveContext context1 = new SolveContext("REQ-1", new int[]{1, 2, 3}, 5);
    SolveContext context2 = new SolveContext("REQ-2", new int[]{1, 2, 3}, 7);
    SolveContext context3 = new SolveContext("REQ-3", new int[]{1, 2, 3}, 5);

    System.out.println("First request:");
    CoinChangeResult result1 = pipeline.execute(context1);
    System.out.println("Result: " + result1.getWays() + " ways\n");

    System.out.println("Different request:");
    CoinChangeResult result2 = pipeline.execute(context2);
    System.out.println("Result: " + result2.getWays() + " ways\n");

    System.out.println("Repeated request (cached):");
    CoinChangeResult result3 = pipeline.execute(context3);
    System.out.println("Result: " + result3.getWays() + " ways\n");
  }

  private static void example3_LazyEvaluation() {
    System.out.println("Example 3: Lazy Evaluation");
    System.out.println("Problem: Defer computation until needed\n");

    LazyResult lazy = new LazyResult(() -> {
      System.out.println("  [Computing...]");
      return CoinChange.solve(new int[]{1, 5, 10}, 20);
    });

    System.out.println("Created lazy result: " + lazy);
    System.out.println("Evaluated: " + lazy.isEvaluated());

    System.out.println("\nAccessing lazy result:");
    int ways = lazy.getWays();
    System.out.println("Result: " + ways + " ways");
    System.out.println("Evaluated: " + lazy.isEvaluated());

    System.out.println("\nAccessing again (no recomputation):");
    int ways2 = lazy.getWays();
    System.out.println("Result: " + ways2 + " ways\n");
  }

  private static void example4_ResultAggregation() {
    System.out.println("Example 4: Result Aggregation & Analytics");
    System.out.println("Problem: Aggregate and analyze multiple results\n");

    ResultAggregator aggregator = new ResultAggregator();

    aggregator.add(CoinChange.solve(new int[]{1, 2, 3}, 5));
    aggregator.add(CoinChange.solve(new int[]{1, 5, 10}, 20));
    aggregator.add(CoinChange.solve(new int[]{1, 2}, 8));
    aggregator.add(CoinChange.solve(new int[]{2, 5}, 10));

    System.out.println(aggregator.generateSummary());
    System.out.println();
  }

  private static void example5_CacheAnalytics() {
    System.out.println("Example 5: Cache Analytics & Efficiency");
    System.out.println("Problem: Monitor cache performance\n");

    CacheMiddleware cacheMiddleware = new CacheMiddleware();
    MiddlewarePipeline pipeline = new MiddlewarePipeline(ctx ->
        CoinChange.solve(ctx.getCoins(), ctx.getTargetSum())
    );
    pipeline.use(cacheMiddleware);

    int[] coins = {1, 5, 10};
    System.out.println("Simulating cache requests...");

    for (int i = 0; i < 5; i++) {
      int sum = (i % 2 == 0) ? 20 : 15;
      SolveContext ctx = new SolveContext("REQ-" + i, coins, sum);
      pipeline.execute(ctx);
      System.out.println("Request " + (i + 1) + " completed");
    }

    System.out.println("\n" + cacheMiddleware.getAnalyticsReport());
    System.out.println();
  }
}
