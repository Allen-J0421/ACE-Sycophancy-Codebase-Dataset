/**
 * The cycle-detection algorithms a caller can choose from at runtime. Use
 * {@link CycleDetector#create(CycleDetectionAlgorithm)} (or {@link #create()})
 * to obtain a detector for a selected value, e.g. parsed from configuration or
 * a command-line argument via {@link #valueOf(String)}.
 */
enum CycleDetectionAlgorithm {

    /** Depth-first search with three-color back-edge detection. */
    DFS("depth-first search with back-edge detection") {
        @Override
        CycleDetector create() {
            return new DfsCycleDetector();
        }
    },

    /** Kahn's algorithm: repeatedly removing in-degree-zero vertices. */
    KAHN("Kahn topological elimination") {
        @Override
        CycleDetector create() {
            return new KahnCycleDetector();
        }
    };

    private final String description;

    CycleDetectionAlgorithm(String description) {
        this.description = description;
    }

    /** A short human-readable description of the algorithm. */
    String description() {
        return description;
    }

    /** Creates a fresh detector implementing this algorithm. */
    abstract CycleDetector create();
}
