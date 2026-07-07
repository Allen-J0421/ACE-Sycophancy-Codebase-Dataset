package graph.cycle;

public class KahnCycleDetectorProvider implements CycleDetectorProvider {
    @Override public Algorithm algorithm() { return Algorithm.KAHN; }
    @Override public CycleDetector create()  { return new KahnCycleDetector(); }
}
