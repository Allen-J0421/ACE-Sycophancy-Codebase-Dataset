class AlgorithmConfig {
    private final int sourceNode;
    private final boolean trackPaths;
    private final boolean verbose;

    private AlgorithmConfig(int sourceNode, boolean trackPaths, boolean verbose) {
        this.sourceNode = sourceNode;
        this.trackPaths = trackPaths;
        this.verbose = verbose;
    }

    static AlgorithmConfig create(int sourceNode) {
        return new AlgorithmConfig(sourceNode, true, false);
    }

    static AlgorithmConfig createVerbose(int sourceNode) {
        return new AlgorithmConfig(sourceNode, true, true);
    }

    int getSourceNode() {
        return sourceNode;
    }

    boolean shouldTrackPaths() {
        return trackPaths;
    }

    boolean isVerbose() {
        return verbose;
    }
}
