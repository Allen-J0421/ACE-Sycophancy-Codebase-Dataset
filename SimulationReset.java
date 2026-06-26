/**
 * Emitted after the simulation is reset to its starting state.
 */
public record SimulationReset(SimulationState state) implements SimulationEvent
{
}
