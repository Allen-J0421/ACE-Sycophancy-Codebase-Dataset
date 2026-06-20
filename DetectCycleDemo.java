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
        DirectedGraph graph = DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(2, 3)
                .build();

        CycleDetectionAlgorithm[] algorithms = args.length > 0
                ? new CycleDetectionAlgorithm[] {CycleDetectionAlgorithm.valueOf(args[0].toUpperCase())}
                : CycleDetectionAlgorithm.values();

        // Attach the console logger to observe detection events; without
        // withLogging(...) the library runs silently.
        CycleDetectionLogger logger = new ConsoleLogger();
        for (CycleDetectionAlgorithm algorithm : algorithms) {
            CycleDetector detector = CycleDetector.create(algorithm).withLogging(logger);
            detector.findCycle(graph);
        }
    }
}
