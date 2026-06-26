/**
 * Emitted when the simulator enters a running state.
 */
public record SimulationStarted(SimulationState state) implements SimulationEvent
{
}
