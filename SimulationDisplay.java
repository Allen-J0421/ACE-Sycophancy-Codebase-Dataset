/**
 * Display interface for the simulation view layer.
 * Receives pre-aggregated {@link FieldStats} from {@link SimulationReporter}
 * so that the view is responsible only for rendering, not for counting.
 *
 * @version 2022.03.02
 */
public interface SimulationDisplay
{
    /**
     * Render the current state of the simulation.
     * @param step        The step number just completed.
     * @param environment The current simulation environment.
     * @param field       The field to render.
     * @param stats       Pre-aggregated population and disease counts for this step.
     */
    void render(int step, Environment environment, Field field, FieldStats stats);
}
