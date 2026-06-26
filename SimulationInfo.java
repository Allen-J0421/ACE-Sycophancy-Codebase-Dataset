import java.util.Map;

/**
 * Holder for cross-run testing/optimisation values used by {@link TestingMain}
 * and {@link Simulator} (to track the most stable probability combination found).
 *
 * Per-species metadata (actor classes, colours, creation probabilities, ...) now
 * lives in the {@link Species} registry.
 */
public class SimulationInfo {

    /**
     * Utility fields to help with the running of TestingMain.
     */
    public static Integer HIGHEST_STEPS = 0;
    public static Map<Class<?>, Double> HIGHEST_STEP_PROBS;

}
