/**
 * Observer interface for simulation state changes.
 *
 * Implementors are notified after each simulation step and consulted
 * to determine whether the simulation remains viable.  Decouples the
 * Simulator from any concrete display or monitoring class.
 *
 * @version 2022.03.01
 */
public interface SimulationObserver
{
    /**
     * Called after every simulation step (including the initial reset).
     * @param event Immutable snapshot of the current simulation state.
     */
    void onStepCompleted(SimulationEvent event);

    /**
     * Whether the simulation should continue running.
     * The simulation halts when any registered observer returns false.
     * @param field The current field state.
     * @return true if the simulation is still viable.
     */
    boolean isViable(Field field);
}
