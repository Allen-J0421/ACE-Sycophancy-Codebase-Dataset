/**
 * Receives events published through the simulation event bus.
 *
 * @version 2022.03.02
 */
public interface SimulationEventListener
{
    void onEvent(SimulationEvent event);
}
