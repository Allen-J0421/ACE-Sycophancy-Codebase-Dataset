import java.util.Optional;

/**
 * Small runnable example of the cycle-detection library.
 *
 * <p>Compile and run from the project root:
 * <pre>{@code
 *   javac *.java && java DetectCycleDemo
 * }</pre>
 */
final class DetectCycleDemo {

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1}, {1, 2}, {2, 0}, {2, 3}
        });

        Optional<Cycle> cycle = new CycleDetector().findCycle(graph);
        System.out.println(cycle
                .map(c -> "true (cycle: " + c + ")")
                .orElse("false"));
    }
}
