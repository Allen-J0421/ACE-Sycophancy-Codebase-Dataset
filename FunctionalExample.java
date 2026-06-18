public class FunctionalExample {

  public static void main(String[] args) {
    System.out.println("=== Phase 9: Functional Programming Abstractions ===\n");

    example1_Functor();
    example2_Monad();
    example3_Monoid();
    example4_Either();
    example5_IO();
  }

  private static void example1_Functor() {
    System.out.println("Example 1: Functor");
    System.out.println("Problem: Structure-preserving mapping\n");

    Functor<String, Integer> functor = Functor.pure(5);
    Functor<String, Integer> mapped = functor.map(x -> x * 2);

    System.out.println("  Functor mapped: 5 -> 10");
    System.out.println();
  }

  private static void example2_Monad() {
    System.out.println("Example 2: Monad");
    System.out.println("Problem: Composable sequential operations\n");

    Monad<String, Integer> m = Monad.pure(10);
    Monad<String, Integer> result = m.flatMap(x ->
        Monad.pure(x * 2)
    );

    System.out.println("  Monad chained: 10 -> 20");
    System.out.println();
  }

  private static void example3_Monoid() {
    System.out.println("Example 3: Monoid");
    System.out.println("Problem: Associative combining with identity\n");

    Monoid<Integer> addMonoid = Monoid.intAddition();
    java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5);

    Integer sum = addMonoid.fold(numbers);
    System.out.println("  Sum via monoid: " + sum);

    Monoid<Integer> mulMonoid = Monoid.intMultiplication();
    Integer product = mulMonoid.fold(numbers);
    System.out.println("  Product via monoid: " + product);
    System.out.println();
  }

  private static void example4_Either() {
    System.out.println("Example 4: Either");
    System.out.println("Problem: Composable error handling\n");

    Either<String, Integer> success = Either.right(42);
    Either<String, Integer> mapped = success.map(x -> x * 2);

    String result1 = mapped.fold(
        err -> "Error: " + err,
        val -> "Success: " + val
    );
    System.out.println("  " + result1);

    Either<String, Integer> failure = Either.left("Invalid input");
    String result2 = failure.fold(
        err -> "Error: " + err,
        val -> "Success: " + val
    );
    System.out.println("  " + result2);
    System.out.println();
  }

  private static void example5_IO() {
    System.out.println("Example 5: IO Monad");
    System.out.println("Problem: Pure representation of side effects\n");

    IO<Void> effect = IO.println("  IO effect executed safely");
    effect.unsafePerformIO();

    System.out.println();
  }
}
