public class UltraAdvancedExample {

  public static void main(String[] args) {
    System.out.println("=== Ultra-Advanced Enterprise Features ===\n");

    example1_HealthMonitoring();
    example2_RateLimiting();
    example3_DistributedTracing();
    example4_ServiceDiscovery();
    example5_Observability();
  }

  private static void example1_HealthMonitoring() {
    System.out.println("Example 1: Health Monitoring System");
    System.out.println("Problem: Monitor system health\n");

    HealthMonitor monitor = new HealthMonitor(5000);

    monitor.register("cache", () ->
        new HealthStatus(HealthStatus.Status.HEALTHY, "Cache operational"));

    monitor.register("algorithm", () ->
        new HealthStatus(HealthStatus.Status.HEALTHY, "Algorithm working"));

    monitor.register("memory", () -> {
      long usedMemory = Runtime.getRuntime().totalMemory() -
                       Runtime.getRuntime().freeMemory();
      long maxMemory = Runtime.getRuntime().maxMemory();
      double usage = (double) usedMemory / maxMemory;

      if (usage > 0.9) {
        return new HealthStatus(HealthStatus.Status.UNHEALTHY,
            "Memory critical: " + (usage * 100) + "%");
      }
      return new HealthStatus(HealthStatus.Status.HEALTHY,
          "Memory usage: " + (usage * 100) + "%");
    });

    System.out.println(monitor.generateHealthReport());
    System.out.println("Overall Status: " + monitor.getOverallStatus());
    System.out.println();
  }

  private static void example2_RateLimiting() {
    System.out.println("Example 2: Rate Limiting");
    System.out.println("Problem: Enforce request limits\n");

    RateLimiter limiter = new RateLimiter(5);  // 5 requests per second

    System.out.println("Submitting 10 requests (limit: 5/sec):");
    for (int i = 1; i <= 10; i++) {
      boolean allowed = limiter.allowRequest();
      System.out.println("  Request " + i + ": " + (allowed ? "ALLOWED" : "BLOCKED"));
    }

    System.out.println("Remaining requests: " + limiter.getRemainingRequests());
    System.out.println();
  }

  private static void example3_DistributedTracing() {
    System.out.println("Example 3: Distributed Tracing");
    System.out.println("Problem: Track requests across system\n");

    MiddlewarePipeline pipeline = new MiddlewarePipeline(ctx ->
        CoinChange.solve(ctx.getCoins(), ctx.getTargetSum())
    );

    pipeline.use(new TracingMiddleware());
    pipeline.use(new LoggingMiddleware(false));

    SolveContext context = new SolveContext("REQ-001", new int[]{1, 5, 10}, 25);
    CoinChangeResult result = pipeline.execute(context);

    TracingContext tracing = TracingMiddleware.getTracingContext(context);
    System.out.println("Trace ID: " + tracing.getTraceId());
    System.out.println("Span ID: " + tracing.getSpanId());
    System.out.println("Duration: " + (tracing.getDurationNanos() / 1_000_000.0) + "ms");
    System.out.println("Tags: " + tracing.toString());
    System.out.println("Result: " + result.getWays() + " ways");
    System.out.println();
  }

  private static void example4_ServiceDiscovery() {
    System.out.println("Example 4: Service Discovery");
    System.out.println("Problem: Locate and describe services\n");

    ServiceRegistry registry = new ServiceRegistry();

    ServiceDescriptor solver = new ServiceDescriptor(
        "CoinChangeSolver", "1.0", "Dynamic programming coin change solver"
    );
    solver.addCapability("multiple-algorithms");
    solver.addCapability("caching");
    solver.addCapability("batch-processing");
    solver.addMetadata("performance", "optimized");
    solver.addMetadata("reliability", "production-grade");

    ServiceDescriptor cache = new ServiceDescriptor(
        "CacheService", "1.0", "Distributed cache provider"
    );
    cache.addCapability("unlimited-cache");
    cache.addCapability("lru-cache");
    cache.addMetadata("backend", "in-memory");

    registry.register(solver);
    registry.register(cache);

    System.out.println(registry.generateRegistry());
  }

  private static void example5_Observability() {
    System.out.println("Example 5: System Observability");
    System.out.println("Problem: Comprehensive system observations\n");

    ObservationCollector collector = new ObservationCollector(100);

    // Simulate observations
    for (int i = 0; i < 5; i++) {
      Observation obs = new Observation(Observation.Type.REQUEST,
          "Processing request " + i);
      obs.addData("request_id", "REQ-" + i);
      obs.addData("timestamp", System.currentTimeMillis());
      collector.collect(obs);
    }

    for (int i = 0; i < 3; i++) {
      Observation obs = new Observation(Observation.Type.CACHE,
          "Cache operation " + i);
      obs.addData("operation", i % 2 == 0 ? "hit" : "miss");
      collector.collect(obs);
    }

    for (int i = 0; i < 2; i++) {
      Observation obs = new Observation(Observation.Type.ALGORITHM,
          "Algorithm execution " + i);
      obs.addData("execution_time", Math.random() * 10);
      collector.collect(obs);
    }

    System.out.println(collector.generateReport());
    System.out.println("Total observations: " + collector.getTotalObservations());
    System.out.println();
  }
}
