package graph.cycle;

public class CycleDetectorFactory {
    private CycleDetectorFactory() {}

    public static CycleDetector create(Algorithm algorithm) {
        switch (algorithm) {
            case KAHN: return new KahnCycleDetector();
            case DFS:  return new DfsCycleDetector();
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
