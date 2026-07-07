package graph.cycle;

public interface CycleDetectorProvider {
    Algorithm algorithm();
    CycleDetector create();
}
