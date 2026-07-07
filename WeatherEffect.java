/**
 * Strategy interface for weather conditions.
 * Each weather type carries its own behavioural parameters so that
 * callers (e.g. Plant) never need to switch on enum constants.
 */
public interface WeatherEffect
{
    /**
     * Returns the probability that a plant will spread during one simulation
     * step under this weather condition.
     */
    double getSpreadProbability();
}
