/**
 * Represents a weather state that can influence actor behaviour.
 * Implementations encode the concrete effects (e.g. how rain affects breeding).
 */
public interface Weather
{
    /**
     * Multiplier applied to a producer's base breeding probability.
     * 1.0 = no change; values below 1.0 suppress breeding.
     */
    double breedingProbabilityModifier();

    /** Human-readable label used by the simulation view. */
    String getDescription();
}
