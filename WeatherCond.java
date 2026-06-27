import java.util.List;
import java.util.Random;
import java.util.Map;
/**
 * The different weather that can occur randomly in the simulator, they all have different attributes which
 * affect the animals and plants in the simulator
 *
 * @version 27.02.22
 */
public enum WeatherCond
{
    Rain (0.6 ,0.5,0.8),
    Fog (0.2,0.5,0.5),
    Sunny (1,1,-0.5),
    Cloudy (0.5,0.5,0.4),
    Windy (0.7,0.5,0.3),
    Storm (0.4,0.5,1);

    private static final Random rand = Randomizer.getRandom();
    private final Map<String, Double> weatherAttributes; 
    private static Time timeOfDay;

    /**
     * Puts attributes of each weather condition in a Map.
     * @param visibility The visibility that the weather condition has.
     * @param brightness The brightness that the weather condition has.
     * @param dampness The dampness that the weather condition has.
     */
    WeatherCond(double visibilty,double brightness,double dampness)
    {
        weatherAttributes = Map.of(
            "visibility", visibilty,
            "brightness", brightness,
            "dampness", dampness
        );
    }

    /**
     * Returns the next weather condition based on the current
     * weather condition and a random generator.
     * @return The next weather condition.
     */
    public WeatherCond nextCondition()
    {
        List<WeatherCond> options = nextConditionsFor(timeOfDay.isDay());
        return options.get(rand.nextInt(options.size()));
    }

    /**
     * Get the possible next weather states for the current state.
     * @param isDay Whether the simulation is currently in daytime.
     * @return The next-state candidates.
     */
    private List<WeatherCond> nextConditionsFor(boolean isDay)
    {
        switch(this) {
            case Sunny:
                return List.of(Cloudy, Windy);
            case Rain:
                return List.of(Storm, Cloudy);
            default:
                return isDay ? List.of(Sunny, Fog, Rain) : List.of(Fog, Rain);
        }
    }

    /**
     * Returns visibility.
     * @return visibility.
     */
    public double getVisibility()
    {
        return weatherAttributes.get("visibility");
    }

    /**
     * Backward-compatible typo-preserving alias for visibility.
     * @return visibility.
     */
    public double getVisibilty()
    {
        return getVisibility();
    }

    /**
     * Returns brightness.
     * @return brightness.
     */
    public double getBrightness()
    {
        return weatherAttributes.get("brightness");
    }
    
    /**
     * Returns dampness.
     * @return dampness.
     */
    public double getDampness()
    {
        return weatherAttributes.get("dampness");
    }
    
    /**
     * Returns the Map of all weather attributes.
     * @return The Map of all weather attributes.
     */
    public Map<String,Double> getWeatherAttributesMap()
    {
        return weatherAttributes;
    }

    /**
     * Sets the timeOfDay field of the weather condition to reference 
     * another Time object
     * @param time The time object referencesd
     */
    public void setTime(Time time)
    {
        timeOfDay = time;
    }
}
