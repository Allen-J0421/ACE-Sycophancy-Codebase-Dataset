/**
 * Dry (no-rain) weather: producers breed at a reduced probability.
 */
public class DryWeather implements Weather
{
    private static final double BREEDING_MODIFIER = 0.2;

    @Override
    public double breedingProbabilityModifier() { return BREEDING_MODIFIER; }

    @Override
    public String getDescription() { return "NOT RAINING"; }
}
