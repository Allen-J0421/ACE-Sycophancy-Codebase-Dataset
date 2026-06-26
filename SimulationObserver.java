/**
 * Observer interface that decouples the simulation engine from the view layer.
 * The engine calls {@link #onStep} after every simulation step and queries
 * {@link #isViable} to decide whether to keep running.
 *
 * @version 2022.03.02
 */
public interface SimulationObserver
{
    /**
     * Called after every simulation step so the observer can update its display.
     * @param step        The step number just completed.
     * @param environment The current simulation environment.
     * @param field       The field after the step.
     */
    void onStep(int step, Environment environment, Field field);

    /**
     * Returns true if the simulation population is still viable and should
     * continue running.
     * @param field The field to inspect.
     */
    boolean isViable(Field field);
}
