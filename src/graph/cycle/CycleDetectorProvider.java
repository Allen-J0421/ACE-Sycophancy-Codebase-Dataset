package graph.cycle;

public interface CycleDetectorProvider {
    Algorithm algorithm();
    CycleDetector create();

    class KahnProvider implements CycleDetectorProvider {
        @Override public Algorithm algorithm() { return Algorithm.KAHN; }
        @Override public CycleDetector create()  { return new KahnCycleDetector(); }
    }

    class DfsProvider implements CycleDetectorProvider {
        @Override public Algorithm algorithm() { return Algorithm.DFS; }
        @Override public CycleDetector create()  { return new DfsCycleDetector(); }
    }
}
