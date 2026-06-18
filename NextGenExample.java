public class NextGenExample {

  public static void main(String[] args) {
    System.out.println("=== Next-Generation Advanced Features ===\n");

    example1_PluginSystem();
    example2_ValidationEngine();
    example3_TransformationPipeline();
    example4_CommandPattern();
    example5_CompositeSolver();
    example6_EventSourcing();
  }

  private static void example1_PluginSystem() {
    System.out.println("Example 1: Plugin System");
    System.out.println("Problem: Dynamically load and manage plugins\n");

    PluginManager manager = new PluginManager();

    Plugin cachePlugin = new Plugin() {
      @Override public String getName() { return "CachePlugin"; }
      @Override public String getVersion() { return "1.0"; }
      @Override public void initialize() { System.out.println("  Cache plugin initialized"); }
      @Override public void shutdown() { System.out.println("  Cache plugin shutdown"); }
      @Override public java.util.Map<String, Object> getMetadata() {
        java.util.Map<String, Object> meta = new java.util.HashMap<>();
        meta.put("type", "caching");
        return meta;
      }
    };

    Plugin metricsPlugin = new Plugin() {
      @Override public String getName() { return "MetricsPlugin"; }
      @Override public String getVersion() { return "1.0"; }
      @Override public void initialize() { System.out.println("  Metrics plugin initialized"); }
      @Override public void shutdown() { System.out.println("  Metrics plugin shutdown"); }
      @Override public java.util.Map<String, Object> getMetadata() {
        java.util.Map<String, Object> meta = new java.util.HashMap<>();
        meta.put("type", "monitoring");
        return meta;
      }
    };

    manager.load(cachePlugin);
    manager.load(metricsPlugin);

    System.out.println("\n" + manager.generatePluginReport());
  }

  private static void example2_ValidationEngine() {
    System.out.println("Example 2: Validation Engine");
    System.out.println("Problem: Extensible validation rules\n");

    ValidationEngine engine = new ValidationEngine();

    engine.addRule(new ValidationRule() {
      @Override public boolean validate(int[] coins, int sum) { return coins != null && coins.length > 0; }
      @Override public String getErrorMessage() { return "Coins array is empty"; }
      @Override public String getRuleName() { return "CoinsNotEmpty"; }
    });

    engine.addRule(new ValidationRule() {
      @Override public boolean validate(int[] coins, int sum) { return sum >= 0; }
      @Override public String getErrorMessage() { return "Sum cannot be negative"; }
      @Override public String getRuleName() { return "SumNonNegative"; }
    });

    int[] coins = {1, 2, 3};
    int sum = 5;

    System.out.println(engine.generateValidationReport(coins, sum));
    System.out.println();
  }

  private static void example3_TransformationPipeline() {
    System.out.println("Example 3: Transformation Pipeline");
    System.out.println("Problem: Chain request transformations\n");

    TransformationPipeline pipeline = new TransformationPipeline();

    pipeline.addTransformer(ctx -> {
      ctx.setAttribute("step1_executed", true);
      System.out.println("  Step 1: Context enriched");
      return ctx;
    });

    pipeline.addTransformer(ctx -> {
      ctx.setAttribute("step2_executed", true);
      System.out.println("  Step 2: Validation applied");
      return ctx;
    });

    pipeline.addTransformer(ctx -> {
      ctx.setAttribute("step3_executed", true);
      System.out.println("  Step 3: Optimization applied");
      return ctx;
    });

    SolveContext context = new SolveContext("REQ-1", new int[]{1, 2, 3}, 5);
    SolveContext transformed = pipeline.transform(context);

    System.out.println("Transformations: " + pipeline.getTransformerCount());
    System.out.println();
  }

  private static void example4_CommandPattern() {
    System.out.println("Example 4: Command Pattern with Undo");
    System.out.println("Problem: Execute and undo operations\n");

    CommandInvoker invoker = new CommandInvoker();

    Command cmd1 = new Command() {
      private boolean executed;
      @Override public void execute() { executed = true; System.out.println("  Solving: coins {1,2,3}, sum 5"); }
      @Override public void undo() { executed = false; System.out.println("  Undone: solve operation"); }
      @Override public String getDescription() { return "Solve coins"; }
    };

    Command cmd2 = new Command() {
      private boolean executed;
      @Override public void execute() { executed = true; System.out.println("  Caching result"); }
      @Override public void undo() { executed = false; System.out.println("  Undone: cache operation"); }
      @Override public String getDescription() { return "Cache result"; }
    };

    invoker.execute(cmd1);
    invoker.execute(cmd2);

    System.out.println("History: " + invoker.getCommandHistory());
    System.out.println("Undoing last command...");
    invoker.undo();
    System.out.println("Last command: " + invoker.getLastCommandDescription());
    System.out.println();
  }

  private static void example5_CompositeSolver() {
    System.out.println("Example 5: Composite Solver with Voting");
    System.out.println("Problem: Combine multiple solvers\n");

    CompositeSolver composite = new CompositeSolver("VerifiedSolver");
    composite.addSolver(new DynamicProgrammingStrategy());
    composite.addSolver(new SpaceOptimizedStrategy());
    composite.addSolver(new DynamicProgrammingStrategy());

    int[] coins = {1, 2, 3};
    int sum = 5;

    System.out.println("Running with " + composite.getSolverCount() + " solvers:");
    java.util.List<Integer> results = composite.solveWithAll(coins, sum);
    System.out.println("  Individual results: " + results);

    int consensus = composite.solveWithVoting(coins, sum);
    System.out.println("  Consensus result: " + consensus + " ways");
    System.out.println();
  }

  private static void example6_EventSourcing() {
    System.out.println("Example 6: Event Sourcing");
    System.out.println("Problem: Track all system events\n");

    EventStore eventStore = new EventStore();

    java.util.Map<String, Object> solveEvent = new java.util.HashMap<>();
    solveEvent.put("coins", new int[]{1, 2, 3});
    solveEvent.put("sum", 5);
    eventStore.append("SOLVE_INITIATED", solveEvent);

    java.util.Map<String, Object> cacheEvent = new java.util.HashMap<>();
    cacheEvent.put("cache_hit", true);
    cacheEvent.put("cached_value", 5);
    eventStore.append("CACHE_HIT", cacheEvent);

    java.util.Map<String, Object> completeEvent = new java.util.HashMap<>();
    completeEvent.put("result", 5);
    completeEvent.put("duration_ms", 1.5);
    eventStore.append("SOLVE_COMPLETED", completeEvent);

    System.out.println(eventStore.generateEventLog());
  }
}
