public class AdvancedFeaturesExample {

  public static void main(String[] args) {
    System.out.println("=== Advanced Features Showcase ===\n");

    example1_EventListeners();
    example2_LRUCaching();
    example3_BatchProcessing();
    example4_BenchmarkComparison();
    example5_EnrichedResults();
  }

  private static void example1_EventListeners() {
    System.out.println("Example 1: Event Listeners & Logging");
    System.out.println("Problem: Coins={1,5,10,25}, Sum=50\n");

    CoinChangeSolver solver = CoinChange.builder()
        .withStrategyType(StrategyType.SPACE_OPTIMIZED)
        .enableMetrics()
        .addListener(new LoggingSolveListener(true))
        .build();

    solver.solve(new int[]{1, 5, 10, 25}, 50);
    System.out.println();
  }

  private static void example2_LRUCaching() {
    System.out.println("Example 2: LRU Cache with Limited Size");
    System.out.println("Problem: Cache max 3 entries, multiple queries\n");

    LRUCachedStrategy lru = new LRUCachedStrategy(new DynamicProgrammingStrategy(), 3);
    CoinChangeSolver solver = new CoinChangeSolver(lru, true);

    int[] coins = {1, 2, 3};
    System.out.println("Query 1: sum=5");
    solver.solve(coins, 5);
    System.out.println("  Cache size: " + lru.getCacheSize());

    System.out.println("Query 2: sum=7");
    solver.solve(coins, 7);
    System.out.println("  Cache size: " + lru.getCacheSize());

    System.out.println("Query 3: sum=9");
    solver.solve(coins, 9);
    System.out.println("  Cache size: " + lru.getCacheSize());

    System.out.println("Query 4: sum=4 (evicts least recently used)");
    solver.solve(coins, 4);
    System.out.println("  Cache size: " + lru.getCacheSize() + " (max: " + lru.getMaxCacheSize() + ")");
    System.out.println();
  }

  private static void example3_BatchProcessing() {
    System.out.println("Example 3: Batch Solving Multiple Problems");

    BatchSolveRequest request = new BatchSolveRequest();
    request.addTask(new int[]{1, 2, 3}, 5);
    request.addTask(new int[]{1, 5, 10}, 20);
    request.addTask(new int[]{2, 5}, 8);

    PerformanceSolveListener perfListener = new PerformanceSolveListener();

    CoinChangeSolver solver = CoinChange.builder()
        .enableMetrics()
        .addListener(new LoggingSolveListener())
        .addListener(perfListener)
        .build();

    System.out.println("Processing " + request.size() + " tasks...");
    BatchSolveResult result = solver.solveBatch(request);

    System.out.println("Result: " + result);
    System.out.println(perfListener);
    System.out.println();
  }

  private static void example4_BenchmarkComparison() {
    System.out.println("Example 4: Strategy Performance Benchmark");

    int[] coins = {1, 2, 5, 10};
    int targetSum = 50;
    int iterations = 1000;

    BenchmarkRunner benchmark = new BenchmarkRunner();

    CoinChangeStrategy standard = new DynamicProgrammingStrategy();
    CoinChangeStrategy optimized = new SpaceOptimizedStrategy();
    CoinChangeStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());

    benchmark.benchmark("Standard 2D DP", standard, coins, targetSum, iterations);
    benchmark.benchmark("Space Optimized 1D DP", optimized, coins, targetSum, iterations);
    benchmark.benchmark("Cached DP", cached, coins, targetSum, iterations);

    benchmark.printResults();
  }

  private static void example5_EnrichedResults() {
    System.out.println("Example 5: Enriched Results with Metadata");

    CoinChangeSolver solver = CoinChange.builder()
        .enableMetrics()
        .build();

    CoinChangeResult result = solver.solve(new int[]{1, 5, 10}, 25);

    if (result instanceof EnrichedCoinChangeResult) {
      EnrichedCoinChangeResult enriched = (EnrichedCoinChangeResult) result;
      enriched.addMetadata("Processed in production environment");
      enriched.addMetadata("Part of batch request #42");

      System.out.println("Result: " + enriched.getWays() + " ways");
      System.out.println("Cached: " + enriched.isCached());
      System.out.println("Created: " + enriched.getCreatedAt());
      System.out.println("Metadata: " + enriched.getMetadata());
      if (enriched.hasMetrics()) {
        System.out.println("Metrics: " + enriched.getMetrics());
      }
    } else {
      System.out.println("Result: " + result);
    }
    System.out.println();
  }
}
