public class UltimateExample {

  public static void main(String[] args) {
    System.out.println("=== Phase 7: Ultimate Patterns ===\n");

    example1_ResultTypes();
    example2_RetryMechanism();
    example3_StateMachine();
    example4_AdaptiveStrategy();
    example5_VisitorPattern();
  }

  private static void example1_ResultTypes() {
    System.out.println("Example 1: Result Types (Success/Failure)");
    System.out.println("Problem: Type-safe error handling\n");

    Result<Integer> success = new Result.Success<>(42);
    Result<Integer> failure = new Result.Failure<>("Something failed");

    System.out.println("Success: " + success);
    System.out.println("Is success: " + success.isSuccess());
    System.out.println("Get value: " + success.getOrElse(0));

    System.out.println("\nFailure: " + failure);
    System.out.println("Is success: " + failure.isSuccess());
    System.out.println("Get value: " + failure.getOrElse(0));

    System.out.println();
  }

  private static void example2_RetryMechanism() {
    System.out.println("Example 2: Retry with Exponential Backoff");
    System.out.println("Problem: Resilient operations\n");

    RetryPolicy policy = new RetryPolicy(3, 100, 2.0);

    Result<Integer> result = policy.execute(() -> {
      System.out.println("  Attempting operation...");
      return 42;
    });

    System.out.println("Result: " + result);
    System.out.println();
  }

  private static void example3_StateMachine() {
    System.out.println("Example 3: State Machine");
    System.out.println("Problem: Track solver state transitions\n");

    StateMachine machine = new StateMachine();

    System.out.println("Initial state: " + machine.getCurrentState());

    machine.transition(SolverState.VALIDATING);
    System.out.println("After validation: " + machine.getCurrentState());

    machine.transition(SolverState.TRANSFORMING);
    machine.transition(SolverState.SOLVING);
    machine.transition(SolverState.CACHING);
    machine.transition(SolverState.COMPLETE);

    System.out.println(machine.generateStateReport());
  }

  private static void example4_AdaptiveStrategy() {
    System.out.println("Example 4: Adaptive Strategy Selection");
    System.out.println("Problem: Select optimal algorithm based on problem size\n");

    // Small problem
    CoinChangeStrategy strategy1 = StrategyFactory.createOptimal(
        new int[]{1, 2, 3}, 10);
    System.out.println("Small problem strategy: " + strategy1.getClass().getSimpleName());

    // Large target sum
    CoinChangeStrategy strategy2 = StrategyFactory.createOptimal(
        new int[]{1, 2, 3}, 2000);
    System.out.println("Large sum strategy: " + strategy2.getClass().getSimpleName());

    // Many coins
    int[] manyCoins = new int[150];
    for (int i = 0; i < 150; i++) manyCoins[i] = i + 1;
    CoinChangeStrategy strategy3 = StrategyFactory.createOptimal(manyCoins, 100);
    System.out.println("Many coins strategy: " + strategy3.getClass().getSimpleName());

    System.out.println();
  }

  private static void example5_VisitorPattern() {
    System.out.println("Example 5: Visitor Pattern");
    System.out.println("Problem: Decouple operations from data structures\n");

    FormattingVisitor visitor = new FormattingVisitor();

    CoinChangeResult result = CoinChange.solve(new int[]{1, 2, 3}, 5);
    visitor.visit(result);

    SolveContext context = new SolveContext("REQ-1", new int[]{1, 2, 3}, 5);
    visitor.visit(context);

    Observation obs = new Observation(Observation.Type.REQUEST, "test");
    visitor.visit(obs);

    System.out.println("Formatted output:");
    System.out.println(visitor.getFormattedOutput());
  }
}
