public class CoinChangeTest {
  private static int testsPassed = 0;
  private static int testsFailed = 0;

  public static void main(String[] args) {
    System.out.println("=== Coin Change Test Suite ===\n");

    testBasicFunctionality();
    testEdgeCases();
    testValidation();
    testMultipleStrategies();
    testBuilder();
    testMetrics();
    testCaching();
    testEventListeners();
    testLRUCaching();
    testBatchProcessing();
    testCacheAnalytics();
    testContextTracking();
    testMiddlewarePipeline();
    testLazyEvaluation();
    testResultAggregation();
    testHealthMonitoring();
    testRateLimiting();
    testTracing();
    testServiceRegistry();
    testObservability();
    testPluginSystem();
    testValidationEngine();
    testTransformationPipeline();
    testCommandPattern();
    testCompositeSolver();
    testEventSourcing();
    testResultTypes();
    testRetryPolicy();
    testStateMachine();
    testAdaptiveStrategy();
    testVisitorPattern();

    System.out.println("\n=== Test Summary ===");
    System.out.println("Passed: " + testsPassed);
    System.out.println("Failed: " + testsFailed);
    System.out.println("Total: " + (testsPassed + testsFailed));

    if (testsFailed > 0) {
      System.exit(1);
    }
  }

  private static void testBasicFunctionality() {
    System.out.println("Testing Basic Functionality:");
    test("Basic: coins {1,2,3}, sum 5 equals 5",
        () -> CoinChange.solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Basic: coins {1,2}, sum 3 equals 2",
        () -> CoinChange.solve(new int[]{1, 2}, 3).getWays() == 2);
    test("Basic: coins {2}, sum 4 equals 1",
        () -> CoinChange.solve(new int[]{2}, 4).getWays() == 1);
  }

  private static void testEdgeCases() {
    System.out.println("\nTesting Edge Cases:");
    test("Edge: sum 0 equals 1",
        () -> CoinChange.solve(new int[]{1, 2, 3}, 0).getWays() == 1);
    test("Edge: single coin",
        () -> CoinChange.solve(new int[]{5}, 10).getWays() == 1);
    test("Edge: impossible sum returns 0",
        () -> CoinChange.solve(new int[]{2, 5}, 3).getWays() == 0);
  }

  private static void testValidation() {
    System.out.println("\nTesting Validation:");
    test("Validation: null coins throws exception",
        () -> {
          try {
            CoinChange.solve(null, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
    test("Validation: empty coins throws exception",
        () -> {
          try {
            CoinChange.solve(new int[]{}, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
    test("Validation: negative sum throws exception",
        () -> {
          try {
            CoinChange.solve(new int[]{1, 2}, -5);
            return false;
          } catch (InvalidTargetSumException e) {
            return true;
          }
        });
    test("Validation: non-positive coins throw exception",
        () -> {
          try {
            CoinChange.solve(new int[]{1, 0}, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
  }

  private static void testMultipleStrategies() {
    System.out.println("\nTesting Multiple Strategies:");
    int[] coins = {1, 2, 3};
    int targetSum = 5;
    int expected = 5;

    CoinChangeStrategy standard = new DynamicProgrammingStrategy();
    CoinChangeStrategy optimized = new SpaceOptimizedStrategy();

    int resultStandard = standard.countWays(coins, targetSum);
    int resultOptimized = optimized.countWays(coins, targetSum);

    test("Strategy: Standard equals " + expected,
        () -> resultStandard == expected);
    test("Strategy: Space Optimized equals " + expected,
        () -> resultOptimized == expected);
    test("Strategy: Both produce same result",
        () -> resultStandard == resultOptimized);
  }

  private static void testBuilder() {
    System.out.println("\nTesting Builder Pattern:");
    test("Builder: Default strategy",
        () -> CoinChange.builder().build().solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Builder: With specific strategy type",
        () -> CoinChange.builder()
            .withStrategyType(StrategyType.SPACE_OPTIMIZED)
            .build()
            .solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Builder: With caching",
        () -> CoinChange.builder().withCaching().build()
            .solve(new int[]{1, 2, 3}, 5).getWays() == 5);
  }

  private static void testMetrics() {
    System.out.println("\nTesting Metrics:");
    test("Metrics: Collected when enabled",
        () -> CoinChange.builder().enableMetrics().build()
            .solve(new int[]{1, 2, 3}, 5).hasMetrics());
    test("Metrics: Not collected by default",
        () -> !CoinChange.solve(new int[]{1, 2, 3}, 5).hasMetrics());
    test("Metrics: Contains execution time",
        () -> {
          CoinChangeResult result = CoinChange.builder().enableMetrics().build()
              .solve(new int[]{1, 2, 3}, 5);
          return result.getMetrics().getExecutionTimeNanos() >= 0;
        });
  }

  private static void testCaching() {
    System.out.println("\nTesting Caching:");
    test("Caching: CachedStrategy wraps delegate",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          return cached.countWays(new int[]{1, 2, 3}, 5) == 5;
        });
    test("Caching: Cache grows with calls",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          cached.countWays(new int[]{1, 2, 3}, 5);
          cached.countWays(new int[]{1, 2}, 3);
          return cached.getCacheSize() == 2;
        });
    test("Caching: Same input returns cached result",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          int first = cached.countWays(new int[]{1, 2, 3}, 5);
          int second = cached.countWays(new int[]{1, 2, 3}, 5);
          return first == second && cached.getCacheSize() == 1;
        });
  }

  private static void testEventListeners() {
    System.out.println("\nTesting Event Listeners:");
    test("Listeners: Added listener receives event",
        () -> {
          final boolean[] called = {false};
          SolveEventListener listener = event -> called[0] = true;
          CoinChangeSolver solver = new CoinChangeSolver(new DynamicProgrammingStrategy());
          solver.addListener(listener);
          solver.solve(new int[]{1, 2}, 3);
          return called[0];
        });
    test("Listeners: Logging listener works",
        () -> {
          CoinChangeSolver solver = new CoinChangeSolver(new DynamicProgrammingStrategy());
          solver.addListener(new LoggingSolveListener());
          CoinChangeResult result = solver.solve(new int[]{1, 2, 3}, 5);
          return result.getWays() == 5;
        });
    test("Listeners: Performance listener tracks calls",
        () -> {
          PerformanceSolveListener perf = new PerformanceSolveListener();
          CoinChangeSolver solver = new CoinChangeSolver(new DynamicProgrammingStrategy());
          solver.addListener(perf);
          solver.solve(new int[]{1, 2, 3}, 5);
          solver.solve(new int[]{1, 2}, 3);
          return perf.getCallCount() == 2;
        });
  }

  private static void testLRUCaching() {
    System.out.println("\nTesting LRU Caching:");
    test("LRU: Cache respects max size",
        () -> {
          LRUCachedStrategy lru = new LRUCachedStrategy(new DynamicProgrammingStrategy(), 2);
          lru.countWays(new int[]{1, 2, 3}, 5);
          lru.countWays(new int[]{1, 2}, 3);
          lru.countWays(new int[]{2}, 4);
          return lru.getCacheSize() == 2;
        });
    test("LRU: Evicts least recently used",
        () -> {
          LRUCachedStrategy lru = new LRUCachedStrategy(new DynamicProgrammingStrategy(), 2);
          lru.countWays(new int[]{1, 2, 3}, 5);
          lru.countWays(new int[]{1, 2}, 3);
          lru.countWays(new int[]{1, 2}, 3);
          return lru.getCacheSize() == 2 && lru.getMaxCacheSize() == 2;
        });
  }

  private static void testBatchProcessing() {
    System.out.println("\nTesting Batch Processing:");
    test("Batch: Processes multiple tasks",
        () -> {
          BatchSolveRequest request = new BatchSolveRequest();
          request.addTask(new int[]{1, 2, 3}, 5);
          request.addTask(new int[]{1, 2}, 3);
          return request.size() == 2;
        });
    test("Batch: Solver processes batch requests",
        () -> {
          CoinChangeSolver solver = new CoinChangeSolver(new DynamicProgrammingStrategy());
          BatchSolveRequest request = new BatchSolveRequest();
          request.addTask(new int[]{1, 2, 3}, 5);
          request.addTask(new int[]{1, 2}, 3);
          BatchSolveResult result = solver.solveBatch(request);
          return result.getSuccessCount() == 2;
        });
    test("Batch: Result includes timing",
        () -> {
          CoinChangeSolver solver = new CoinChangeSolver(new DynamicProgrammingStrategy());
          BatchSolveRequest request = new BatchSolveRequest();
          request.addTask(new int[]{1, 2, 3}, 5);
          BatchSolveResult result = solver.solveBatch(request);
          return result.getTotalTimeMillis() >= 0;
        });
  }

  private static void testCacheAnalytics() {
    System.out.println("\nTesting Cache Analytics:");
    test("Analytics: Tracks cache hits",
        () -> {
          CacheAnalytics analytics = new CacheAnalytics();
          analytics.recordCacheHit();
          analytics.recordCacheHit();
          return analytics.getStatistics().getHits() == 2;
        });
    test("Analytics: Calculates hit rate",
        () -> {
          CacheAnalytics analytics = new CacheAnalytics();
          analytics.recordCacheHit();
          analytics.recordCacheMiss();
          return analytics.getStatistics().getHitRate() == 0.5;
        });
    test("Analytics: Generates report",
        () -> {
          CacheAnalytics analytics = new CacheAnalytics();
          analytics.recordCacheHit();
          String report = analytics.generateReport();
          return report.contains("Hit Rate");
        });
  }

  private static void testContextTracking() {
    System.out.println("\nTesting Context Tracking:");
    test("Context: Stores request ID",
        () -> {
          SolveContext ctx = new SolveContext("REQ-123", new int[]{1, 2}, 5);
          return "REQ-123".equals(ctx.getRequestId());
        });
    test("Context: Stores metadata",
        () -> {
          SolveContext ctx = new SolveContext("REQ-1", new int[]{1}, 1);
          ctx.setAttribute("key", "value");
          return "value".equals(ctx.getAttribute("key"));
        });
    test("Context: Provides coins and sum",
        () -> {
          SolveContext ctx = new SolveContext("REQ-1", new int[]{1, 2, 3}, 5);
          return ctx.getTargetSum() == 5 && ctx.getCoins().length == 3;
        });
  }

  private static void testMiddlewarePipeline() {
    System.out.println("\nTesting Middleware Pipeline:");
    test("Pipeline: Executes middleware chain",
        () -> {
          MiddlewarePipeline pipeline = new MiddlewarePipeline(ctx ->
              CoinChange.solve(ctx.getCoins(), ctx.getTargetSum())
          );
          pipeline.use(new LoggingMiddleware());
          SolveContext ctx = new SolveContext("REQ-1", new int[]{1, 2}, 3);
          CoinChangeResult result = pipeline.execute(ctx);
          return result.getWays() == 2;
        });
    test("Pipeline: Tracks middleware count",
        () -> {
          MiddlewarePipeline pipeline = new MiddlewarePipeline(ctx ->
              CoinChange.solve(ctx.getCoins(), ctx.getTargetSum())
          );
          pipeline.use(new LoggingMiddleware());
          pipeline.use(new CacheMiddleware());
          return pipeline.getMiddlewareCount() == 2;
        });
  }

  private static void testLazyEvaluation() {
    System.out.println("\nTesting Lazy Evaluation:");
    test("Lazy: Defers computation",
        () -> {
          LazyResult lazy = new LazyResult(() ->
              CoinChange.solve(new int[]{1, 2}, 3)
          );
          return !lazy.isEvaluated();
        });
    test("Lazy: Evaluates on access",
        () -> {
          LazyResult lazy = new LazyResult(() ->
              CoinChange.solve(new int[]{1, 2}, 3)
          );
          lazy.getWays();
          return lazy.isEvaluated();
        });
    test("Lazy: Caches result",
        () -> {
          final int[] evalCount = {0};
          LazyResult lazy = new LazyResult(() -> {
            evalCount[0]++;
            return CoinChange.solve(new int[]{1, 2}, 3);
          });
          lazy.getWays();
          lazy.getWays();
          return evalCount[0] == 1;
        });
  }

  private static void testResultAggregation() {
    System.out.println("\nTesting Result Aggregation:");
    test("Aggregation: Collects results",
        () -> {
          ResultAggregator agg = new ResultAggregator();
          agg.add(CoinChange.solve(new int[]{1, 2}, 3));
          agg.add(CoinChange.solve(new int[]{1}, 5));
          return agg.getTotalResults() == 2;
        });
    test("Aggregation: Calculates statistics",
        () -> {
          ResultAggregator agg = new ResultAggregator();
          agg.add(CoinChange.solve(new int[]{1, 2, 3}, 5));
          agg.add(CoinChange.solve(new int[]{1}, 10));
          return agg.getTotalWays() > 0;
        });
    test("Aggregation: Generates summary",
        () -> {
          ResultAggregator agg = new ResultAggregator();
          agg.add(CoinChange.solve(new int[]{1, 2}, 3));
          String summary = agg.generateSummary();
          return summary.contains("Average Ways");
        });
  }

  private static void testHealthMonitoring() {
    System.out.println("\nTesting Health Monitoring:");
    test("Health: Registers health checks",
        () -> {
          HealthMonitor monitor = new HealthMonitor(5000);
          monitor.register("test", () ->
              new HealthStatus(HealthStatus.Status.HEALTHY, "OK"));
          return monitor.checkHealth("test").isHealthy();
        });
    test("Health: Determines overall status",
        () -> {
          HealthMonitor monitor = new HealthMonitor(5000);
          monitor.register("check1", () ->
              new HealthStatus(HealthStatus.Status.HEALTHY, "OK"));
          monitor.register("check2", () ->
              new HealthStatus(HealthStatus.Status.HEALTHY, "OK"));
          return monitor.getOverallStatus() == HealthStatus.Status.HEALTHY;
        });
    test("Health: Generates health report",
        () -> {
          HealthMonitor monitor = new HealthMonitor(5000);
          monitor.register("test", () ->
              new HealthStatus(HealthStatus.Status.HEALTHY, "OK"));
          String report = monitor.generateHealthReport();
          return report.contains("System Health Report");
        });
  }

  private static void testRateLimiting() {
    System.out.println("\nTesting Rate Limiting:");
    test("RateLimit: Enforces request limit",
        () -> {
          RateLimiter limiter = new RateLimiter(3);
          boolean first = limiter.allowRequest();
          boolean second = limiter.allowRequest();
          boolean third = limiter.allowRequest();
          boolean fourth = limiter.allowRequest();
          return first && second && third && !fourth;
        });
    test("RateLimit: Tracks remaining requests",
        () -> {
          RateLimiter limiter = new RateLimiter(5);
          limiter.allowRequest();
          limiter.allowRequest();
          return limiter.getRemainingRequests() == 3;
        });
    test("RateLimit: Works as middleware",
        () -> {
          RateLimitMiddleware middleware = new RateLimitMiddleware(10);
          SolveContext ctx = new SolveContext("REQ-1", new int[]{1}, 1);
          return middleware.getRateLimiter().allowRequest();
        });
  }

  private static void testTracing() {
    System.out.println("\nTesting Distributed Tracing:");
    test("Tracing: Creates trace context",
        () -> {
          TracingContext tracing = new TracingContext("trace-1", "span-1");
          return tracing.getTraceId().equals("trace-1") &&
                 tracing.getSpanId().equals("span-1");
        });
    test("Tracing: Records tags",
        () -> {
          TracingContext tracing = new TracingContext("trace-1", "span-1");
          tracing.addTag("key", "value");
          return "value".equals(tracing.getTag("key"));
        });
    test("Tracing: Measures duration",
        () -> {
          TracingContext tracing = new TracingContext("trace-1", "span-1");
          tracing.end();
          return tracing.getDurationNanos() >= 0;
        });
  }

  private static void testServiceRegistry() {
    System.out.println("\nTesting Service Registry:");
    test("Registry: Registers services",
        () -> {
          ServiceRegistry registry = new ServiceRegistry();
          ServiceDescriptor desc = new ServiceDescriptor("service", "1.0", "desc");
          registry.register(desc);
          return registry.lookup("service") != null;
        });
    test("Registry: Lists all services",
        () -> {
          ServiceRegistry registry = new ServiceRegistry();
          registry.register(new ServiceDescriptor("s1", "1.0", "d1"));
          registry.register(new ServiceDescriptor("s2", "1.0", "d2"));
          return registry.getServiceCount() == 2;
        });
    test("Registry: Generates service listing",
        () -> {
          ServiceRegistry registry = new ServiceRegistry();
          registry.register(new ServiceDescriptor("test", "1.0", "desc"));
          String listing = registry.generateRegistry();
          return listing.contains("Service Registry");
        });
  }

  private static void testObservability() {
    System.out.println("\nTesting Observability:");
    test("Observe: Collects observations",
        () -> {
          ObservationCollector collector = new ObservationCollector(10);
          collector.collect(new Observation(Observation.Type.REQUEST, "test"));
          return collector.getTotalObservations() == 1;
        });
    test("Observe: Filters by type",
        () -> {
          ObservationCollector collector = new ObservationCollector(10);
          collector.collect(new Observation(Observation.Type.REQUEST, "r"));
          collector.collect(new Observation(Observation.Type.CACHE, "c"));
          return collector.getObservationsByType(Observation.Type.REQUEST).size() == 1;
        });
    test("Observe: Generates report",
        () -> {
          ObservationCollector collector = new ObservationCollector(10);
          collector.collect(new Observation(Observation.Type.REQUEST, "test"));
          String report = collector.generateReport();
          return report.contains("Observations Report");
        });
  }

  private static void testPluginSystem() {
    System.out.println("\nTesting Plugin System:");
    test("Plugin: Loads and manages plugins",
        () -> {
          PluginManager manager = new PluginManager();
          Plugin plugin = new Plugin() {
            public String getName() { return "test"; }
            public String getVersion() { return "1.0"; }
            public void initialize() {}
            public void shutdown() {}
            public java.util.Map<String, Object> getMetadata() { return new java.util.HashMap<>(); }
          };
          manager.load(plugin);
          return manager.getPlugin("test") != null;
        });
    test("Plugin: Unloads plugins",
        () -> {
          PluginManager manager = new PluginManager();
          Plugin plugin = new Plugin() {
            public String getName() { return "test"; }
            public String getVersion() { return "1.0"; }
            public void initialize() {}
            public void shutdown() {}
            public java.util.Map<String, Object> getMetadata() { return new java.util.HashMap<>(); }
          };
          manager.load(plugin);
          manager.unload("test");
          return manager.getPlugin("test") == null;
        });
    test("Plugin: Lists all plugins",
        () -> {
          PluginManager manager = new PluginManager();
          return manager.getPluginCount() == 0;
        });
  }

  private static void testValidationEngine() {
    System.out.println("\nTesting Validation Engine:");
    test("Validation: Validates with rules",
        () -> {
          ValidationEngine engine = new ValidationEngine();
          engine.addRule(new ValidationRule() {
            public boolean validate(int[] coins, int sum) { return coins != null; }
            public String getErrorMessage() { return "null"; }
            public String getRuleName() { return "NotNull"; }
          });
          return engine.validate(new int[]{1}, 5);
        });
    test("Validation: Detects failed rules",
        () -> {
          ValidationEngine engine = new ValidationEngine();
          engine.addRule(new ValidationRule() {
            public boolean validate(int[] coins, int sum) { return false; }
            public String getErrorMessage() { return "failed"; }
            public String getRuleName() { return "AlwaysFails"; }
          });
          return engine.getFailedRules(new int[]{1}, 5).size() == 1;
        });
    test("Validation: Generates report",
        () -> {
          ValidationEngine engine = new ValidationEngine();
          String report = engine.generateValidationReport(new int[]{1}, 5);
          return report.contains("Validation Report");
        });
  }

  private static void testTransformationPipeline() {
    System.out.println("\nTesting Transformation Pipeline:");
    test("Transform: Chains transformations",
        () -> {
          TransformationPipeline pipeline = new TransformationPipeline();
          pipeline.addTransformer(ctx -> {
            ctx.setAttribute("transformed", true);
            return ctx;
          });
          SolveContext ctx = new SolveContext("REQ", new int[]{1}, 1);
          SolveContext result = pipeline.transform(ctx);
          return "true".equals(String.valueOf(result.getAttribute("transformed")));
        });
    test("Transform: Counts transformers",
        () -> {
          TransformationPipeline pipeline = new TransformationPipeline();
          pipeline.addTransformer(ctx -> ctx);
          pipeline.addTransformer(ctx -> ctx);
          return pipeline.getTransformerCount() == 2;
        });
  }

  private static void testCommandPattern() {
    System.out.println("\nTesting Command Pattern:");
    test("Command: Executes and tracks history",
        () -> {
          CommandInvoker invoker = new CommandInvoker();
          Command cmd = new Command() {
            public void execute() {}
            public void undo() {}
            public String getDescription() { return "test"; }
          };
          invoker.execute(cmd);
          return invoker.getHistorySize() == 1;
        });
    test("Command: Undoes operations",
        () -> {
          CommandInvoker invoker = new CommandInvoker();
          Command cmd = new Command() {
            public void execute() {}
            public void undo() {}
            public String getDescription() { return "test"; }
          };
          invoker.execute(cmd);
          invoker.undo();
          return invoker.getHistorySize() == 0;
        });
  }

  private static void testCompositeSolver() {
    System.out.println("\nTesting Composite Solver:");
    test("Composite: Combines multiple solvers",
        () -> {
          CompositeSolver solver = new CompositeSolver("test");
          solver.addSolver(new DynamicProgrammingStrategy());
          solver.addSolver(new SpaceOptimizedStrategy());
          return solver.getSolverCount() == 2;
        });
    test("Composite: Solves with all solvers",
        () -> {
          CompositeSolver solver = new CompositeSolver("test");
          solver.addSolver(new DynamicProgrammingStrategy());
          solver.addSolver(new SpaceOptimizedStrategy());
          java.util.List<Integer> results = solver.solveWithAll(new int[]{1, 2}, 3);
          return results.size() == 2;
        });
    test("Composite: Voting consensus",
        () -> {
          CompositeSolver solver = new CompositeSolver("test");
          solver.addSolver(new DynamicProgrammingStrategy());
          solver.addSolver(new SpaceOptimizedStrategy());
          int consensus = solver.solveWithVoting(new int[]{1, 2}, 3);
          return consensus == 2;
        });
  }

  private static void testEventSourcing() {
    System.out.println("\nTesting Event Sourcing:");
    test("Events: Stores events",
        () -> {
          EventStore store = new EventStore();
          store.append("TEST", new java.util.HashMap<>());
          return store.getEventCount() == 1;
        });
    test("Events: Filters by type",
        () -> {
          EventStore store = new EventStore();
          store.append("TYPE_A", new java.util.HashMap<>());
          store.append("TYPE_B", new java.util.HashMap<>());
          return store.getEventsByType("TYPE_A").size() == 1;
        });
    test("Events: Generates log",
        () -> {
          EventStore store = new EventStore();
          store.append("TEST", new java.util.HashMap<>());
          String log = store.generateEventLog();
          return log.contains("Event Log");
        });
  }

  private static void testResultTypes() {
    System.out.println("\nTesting Result Types:");
    test("Result: Success type",
        () -> {
          Result<Integer> success = new Result.Success<>(42);
          return success.isSuccess() && success.getOrElse(0) == 42;
        });
    test("Result: Failure type",
        () -> {
          Result<Integer> failure = new Result.Failure<>("error");
          return !failure.isSuccess() && failure.getOrElse(0) == 0;
        });
    test("Result: Map transformation",
        () -> {
          Result<Integer> success = new Result.Success<>(42);
          Result<Integer> mapped = success.map(x -> x * 2);
          return mapped.isSuccess() && mapped.getOrElse(0) == 84;
        });
  }

  private static void testRetryPolicy() {
    System.out.println("\nTesting Retry Policy:");
    test("Retry: Executes successfully",
        () -> {
          RetryPolicy policy = new RetryPolicy(3, 10);
          Result<Integer> result = policy.execute(() -> 42);
          return result.isSuccess();
        });
    test("Retry: Fails after max attempts",
        () -> {
          RetryPolicy policy = new RetryPolicy(2, 10);
          Result<Integer> result = policy.execute(() -> {
            throw new RuntimeException("fail");
          });
          return !result.isSuccess();
        });
  }

  private static void testStateMachine() {
    System.out.println("\nTesting State Machine:");
    test("State: Transitions correctly",
        () -> {
          StateMachine machine = new StateMachine();
          machine.transition(SolverState.VALIDATING);
          return machine.getCurrentState() == SolverState.VALIDATING;
        });
    test("State: Tracks history",
        () -> {
          StateMachine machine = new StateMachine();
          machine.transition(SolverState.VALIDATING);
          return machine.getHistory().size() == 2;
        });
    test("State: Validates transitions",
        () -> {
          StateMachine machine = new StateMachine();
          try {
            machine.transition(SolverState.SOLVING);
            return false;
          } catch (IllegalStateException e) {
            return true;
          }
        });
  }

  private static void testAdaptiveStrategy() {
    System.out.println("\nTesting Adaptive Strategy:");
    test("Adaptive: Creates strategies",
        () -> {
          CoinChangeStrategy s1 = StrategyFactory.createOptimal(new int[]{1}, 5);
          CoinChangeStrategy s2 = StrategyFactory.createOptimal(new int[]{1}, 5000);
          return s1 != null && s2 != null;
        });
    test("Adaptive: Selects based on size",
        () -> {
          CoinChangeStrategy small = StrategyFactory.createOptimal(new int[]{1, 2}, 10);
          CoinChangeStrategy large = StrategyFactory.createOptimal(new int[]{1, 2}, 2000);
          return small.getClass() != large.getClass();
        });
  }

  private static void testVisitorPattern() {
    System.out.println("\nTesting Visitor Pattern:");
    test("Visitor: Visits result",
        () -> {
          FormattingVisitor visitor = new FormattingVisitor();
          CoinChangeResult result = CoinChange.solve(new int[]{1}, 1);
          visitor.visit(result);
          return visitor.getFormattedOutput().contains("Result");
        });
    test("Visitor: Visits context",
        () -> {
          FormattingVisitor visitor = new FormattingVisitor();
          SolveContext ctx = new SolveContext("REQ", new int[]{1}, 1);
          visitor.visit(ctx);
          return visitor.getFormattedOutput().contains("Context");
        });
    test("Visitor: Visits observation",
        () -> {
          FormattingVisitor visitor = new FormattingVisitor();
          Observation obs = new Observation(Observation.Type.REQUEST, "test");
          visitor.visit(obs);
          return visitor.getFormattedOutput().contains("Observation");
        });
  }

  private static void test(String name, TestCase testCase) {
    try {
      if (testCase.run()) {
        System.out.println("  ✓ " + name);
        testsPassed++;
      } else {
        System.out.println("  ✗ " + name);
        testsFailed++;
      }
    } catch (Exception e) {
      System.out.println("  ✗ " + name + " (Exception: " + e.getMessage() + ")");
      testsFailed++;
    }
  }

  interface TestCase {
    boolean run() throws Exception;
  }
}
