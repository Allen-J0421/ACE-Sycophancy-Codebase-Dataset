/**
 * Rainy weather: producers breed at their full base probability.
 */
public class RainyWeather implements Weather
{
    @Override
    public double breedingProbabilityModifier() { return 1.0; }

    @Override
    public String getDescription() { return "RAINING"; }
}
