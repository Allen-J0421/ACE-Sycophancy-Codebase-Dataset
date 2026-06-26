/**
 * Emitted when the simulator exits a running state.
 */
public record SimulationStopped(SimulationState state) implements SimulationEvent
{
}
