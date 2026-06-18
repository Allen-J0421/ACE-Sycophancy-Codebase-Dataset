public class Java21FeaturesDemo {
    public static void main(String[] args) {
        demonstrateJava21Records();
        demonstrateJava21PatternMatching();
        demonstrateJava21Streams();
        demonstrateUtilities();
    }

    private static void demonstrateJava21Records() {
        System.out.println("=== JAVA 21 RECORDS FEATURE ===\n");

        SortResult result1 = new SortResult(1_500_000L, 25L, 30L, true, "Fast sort");
        SortResult result2 = new SortResult(2_000_000L, 30L, 35L, true, "Standard sort");

        System.out.println("Immutable Record with validation:");
        System.out.println("  Result 1: " + result1);
        System.out.println("  Result 2: " + result2);

        System.out.println("\nRecord methods:");
        System.out.println("  result1.durationMs(): " + result1.durationMs());
        System.out.println("  result1.comparisonsPerElement(100): " + result1.comparisonsPerElement(100));

        try {
            new SortResult(-1, 0, 0, true, "Invalid");
        } catch (IllegalArgumentException e) {
            System.out.println("\nValidation enforced: " + e.getMessage());
        }
        System.out.println();
    }

    private static void demonstrateJava21PatternMatching() {
        System.out.println("=== JAVA 21 PATTERN MATCHING ===\n");

        SortEventType[] events = {
                new SortStartedEvent("Beginning sort"),
                new SortCompletedEvent("Sort done",
                        new SortResult(1_500_000L, 20L, 25L, true, "Success")),
                new SortFailedEvent("Error occurred", new RuntimeException("Out of memory")),
                new ValidationFailedEvent("Invalid input", new IllegalArgumentException("Null array"))
        };

        System.out.println("Pattern matching on sealed types:");
        for (SortEventType event : events) {
            String message = switch (event) {
                case SortStartedEvent e ->
                        "✓ Started: " + e.message();
                case SortCompletedEvent e ->
                        "✓ Completed: " + String.format("%.2f ms", e.result().durationMs());
                case SortFailedEvent e ->
                        "✗ Failed: " + e.cause().getClass().getSimpleName();
                case ValidationFailedEvent e ->
                        "⚠ Invalid: " + e.message();
            };
            System.out.println("  " + message);
        }
        System.out.println();
    }

    private static void demonstrateJava21Streams() {
        System.out.println("=== JAVA 21 STREAMS & FUNCTIONAL ===\n");

        // Create sort results
        java.util.List<SortResult> results = new java.util.ArrayList<>();
        results.add(new SortResult(1_500_000L, 20L, 25L, true, "Run 1"));
        results.add(new SortResult(1_600_000L, 21L, 26L, true, "Run 2"));
        results.add(new SortResult(1_400_000L, 19L, 24L, true, "Run 3"));

        System.out.println("Stream operations on results:");

        double avgDuration = results.stream()
                .mapToDouble(SortResult::durationMs)
                .average()
                .orElse(0.0);
        System.out.println("  Average duration: " + String.format("%.3f ms", avgDuration));

        long totalComparisons = results.stream()
                .mapToLong(SortResult::comparisons)
                .sum();
        System.out.println("  Total comparisons: " + totalComparisons);

        var fastest = results.stream()
                .min((a, b) -> Long.compare(a.durationNanos(), b.durationNanos()));
        fastest.ifPresent(r -> System.out.println("  Fastest: " + String.format("%.3f ms", r.durationMs())));

        System.out.println();
    }

    private static void demonstrateUtilities() {
        System.out.println("=== UTILITY METHODS ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99, 12, 8};

        System.out.println("Before: " + formatArray(data));
        System.out.println("Is sorted: " + SortingUtils.verifySorted(data));

        SortResult result = SortingUtils.sortAndMeasure(data);
        System.out.println("After: " + formatArray(data));
        System.out.println("Is sorted: " + SortingUtils.verifySorted(data));
        System.out.println("Result: " + result);

        System.out.println();
    }

    private static <T> String formatArray(T[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(array.length, 8); i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        if (array.length > 8) sb.append(", ...");
        sb.append("]");
        return sb.toString();
    }
}
