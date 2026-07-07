package graph.cycle;

public class DfsCycleDetectorProvider implements CycleDetectorProvider {
    @Override public Algorithm algorithm() { return Algorithm.DFS; }
    @Override public CycleDetector create()  { return new DfsCycleDetector(); }
}
