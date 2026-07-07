package graph.cycle;

public class CycleDetectorFactory {
    private CycleDetectorFactory() {}

    public static CycleDetector create(Algorithm algorithm) {
        switch (algorithm) {
            case KAHN: return new KahnCycleDetector();
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
