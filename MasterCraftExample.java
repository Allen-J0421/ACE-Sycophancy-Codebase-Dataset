public class MasterCraftExample {

  public static void main(String[] args) {
    System.out.println("=== Phase 8: Master-Craft Patterns ===\n");

    example1_InterceptorChain();
    example2_Specification();
    example3_Adapter();
    example4_Proxy();
  }

  private static void example1_InterceptorChain() {
    System.out.println("Example 1: Interceptor Chain");
    System.out.println("Problem: Pre/post-processing pipeline\n");

    java.util.List<Interceptor> interceptors = new java.util.ArrayList<>();

    interceptors.add((chain, request) -> {
      System.out.println("  [Interceptor 1] Processing: " + request);
      return chain.proceed(request);
    });

    interceptors.add((chain, request) -> {
      System.out.println("  [Interceptor 2] Logging: " + request);
      return chain.proceed(request);
    });

    interceptors.add((chain, request) -> {
      System.out.println("  [Interceptor 3] Validation: " + request);
      return "Result from interceptor 3";
    });

    Object result = InterceptorChain.executeChain(interceptors, "initial_request");
    System.out.println("  Final result: " + result);
    System.out.println();
  }

  private static void example2_Specification() {
    System.out.println("Example 2: Specification Pattern");
    System.out.println("Problem: Composable business rules\n");

    Specification<Integer> isPositive = new Specification<Integer>() {
      @Override public boolean isSatisfiedBy(Integer candidate) {
        return candidate > 0;
      }
      @Override public String getDescription() { return "Positive"; }
    };

    Specification<Integer> isEven = new Specification<Integer>() {
      @Override public boolean isSatisfiedBy(Integer candidate) {
        return candidate % 2 == 0;
      }
      @Override public String getDescription() { return "Even"; }
    };

    Specification<Integer> isLarge = new Specification<Integer>() {
      @Override public boolean isSatisfiedBy(Integer candidate) {
        return candidate > 100;
      }
      @Override public String getDescription() { return "Large"; }
    };

    // Compose specifications
    Specification<Integer> spec = isPositive.and(isEven).or(isLarge);

    System.out.println("Specification: " + spec.getDescription());
    System.out.println("5 satisfies: " + spec.isSatisfiedBy(5));
    System.out.println("10 satisfies: " + spec.isSatisfiedBy(10));
    System.out.println("150 satisfies: " + spec.isSatisfiedBy(150));
    System.out.println();
  }

  private static void example3_Adapter() {
    System.out.println("Example 3: Adapter Pattern");
    System.out.println("Problem: Adapt incompatible interfaces\n");

    CoinChangeStrategy standard = new DynamicProgrammingStrategy();
    CoinChangeStrategy adapted = new Adapter(standard);

    int result = adapted.countWays(new int[]{1, 2, 3}, 5);
    System.out.println("Adapted strategy result: " + result);
    System.out.println();
  }

  private static void example4_Proxy() {
    System.out.println("Example 4: Proxy Pattern");
    System.out.println("Problem: Access control and quota enforcement\n");

    CoinChangeStrategy realStrategy = new DynamicProgrammingStrategy();
    Proxy proxied = new Proxy(realStrategy, 5);

    for (int i = 1; i <= 4; i++) {
      int result = proxied.countWays(new int[]{1, 2, 3}, i + 4);
      System.out.println("  Call " + i + ": " + result + " (Usage: " +
          String.format("%.0f", proxied.getUsagePercentage()) + "%)");
    }

    try {
      proxied.countWays(new int[]{1, 2, 3}, 10);  // This will exceed limit
    } catch (RuntimeException e) {
      System.out.println("  Error: " + e.getMessage());
    }
    System.out.println();
  }
}
