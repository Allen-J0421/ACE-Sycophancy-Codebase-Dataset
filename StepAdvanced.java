/**
 * Emitted after the simulation successfully advances by one step.
 */
public record StepAdvanced(SimulationState state) implements SimulationEvent
{
}
