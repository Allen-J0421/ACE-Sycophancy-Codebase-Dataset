/**
 * Service that manages the storm lifecycle for a simulation run. Owns the
 * storm trigger probability, decides each step whether a storm occurs, and
 * delegates the actual creature-kill logic to the underlying Weather object.
 * SimulationEngine retains control over when tryStorm() is invoked.
 *
 * @version 2022/03/02
 */
public class WeatherManager
{
    private static final int STORM_SCOPE = 3;

    private final Weather weather;
    private final double stormHappenProbability;

    /**
     * Create a WeatherManager for the given field.
     * @param field               The field on which storms occur.
     * @param stormHappenProbability Probability that a storm is triggered each step.
     */
    public WeatherManager(Field field, double stormHappenProbability)
    {
        weather = new Weather(field);
        this.stormHappenProbability = stormHappenProbability;
    }

    /** Returns the underlying Weather object consumed by the view. */
    public Weather getWeather() { return weather; }

    /**
     * Decide whether a storm occurs this step. If the probability check passes,
     * triggers an underwater storm and marks the storm as active; otherwise
     * marks it as inactive. Called by SimulationEngine once per step.
     */
    public void tryStorm()
    {
        if (Randomizer.getRandom().nextDouble() <= stormHappenProbability) {
            weather.underwaterStorm(STORM_SCOPE);
            weather.setStormStart(true);
        } else {
            weather.setStormStart(false);
        }
    }
}
