/**
 * Observer for simulation state changes.
 */
public interface SimulationListener
{
    default void stepCompleted(SimulationEvent event)
    {
    }

    default void simulationReset(SimulationEvent event)
    {
    }

    default void populationChanged(SimulationEvent event)
    {
    }
}
