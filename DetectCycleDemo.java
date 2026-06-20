/**
 * Small runnable example showing runtime selection of the detection algorithm.
 *
 * <p>Run from the project root, optionally naming an algorithm:
 * <pre>{@code
 *   javac *.java
 *   java DetectCycleDemo          # runs every available algorithm
 *   java DetectCycleDemo KAHN     # runs only the named one
 * }</pre>
 */
final class DetectCycleDemo {

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1}, {1, 2}, {2, 0}, {2, 3}
        });

        CycleDetectionAlgorithm[] algorithms = args.length > 0
                ? new CycleDetectionAlgorithm[] {CycleDetectionAlgorithm.valueOf(args[0].toUpperCase())}
                : CycleDetectionAlgorithm.values();

        for (CycleDetectionAlgorithm algorithm : algorithms) {
            CycleDetector detector = CycleDetector.create(algorithm);
            String result = detector.findCycle(graph)
                    .map(cycle -> "cycle " + cycle)
                    .orElse("acyclic");
            System.out.printf("%-4s (%s): %s%n", algorithm, algorithm.description(), result);
        }
    }
}
