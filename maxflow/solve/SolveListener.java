package maxflow.solve;

import maxflow.path.AugmentingPath;

/**
 * A hook for observing the progress of a {@link MaxFlowSolver}, intended for logging
 * and instrumentation. Implementations should be cheap and side-effect-light, as the
 * solver calls them on its hot path.
 *
 * <p>This is a functional interface, so a simple listener can be a lambda
 * ({@code path -> log(path)}). {@link AugmentationCounter} is the built-in implementation
 * used to compare strategies. Further events, should they be needed, can be added later as
 * default methods without breaking existing listeners.
 */
@FunctionalInterface
public interface SolveListener {

    /**
     * Invoked once for each augmenting path the solver discovers and applies — i.e. once
     * per augmentation. The final, unsuccessful search that ends the computation is not
     * reported, so counting these calls yields the number of augmentations performed.
     */
    void onAugmentingPath(AugmentingPath path);

    /** A listener that ignores every event. */
    SolveListener NONE = path -> {
    };
}
