import java.util.Random;
import java.util.EnumMap;
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
    private static final Map<WeatherCond, WeatherCond[]> NIGHT_TRANSITIONS = createNightTransitions();
    private static final Map<WeatherCond, WeatherCond[]> DAY_TRANSITIONS = createDayTransitions();
    private final Map<WeatherAttribute, Double> weatherAttributes; 
    private static Time timeOfDay;

    /**
     * Puts attributes of each weather condition in a Map.
     * @param visibility The visibility that the weather condition has.
     * @param brightness The brightness that the weather condition has.
     * @param dampness The dampness that the weather condition has.
     */
    WeatherCond(double visibilty,double brightness,double dampness)
    {
        weatherAttributes = new EnumMap<>(WeatherAttribute.class);
        weatherAttributes.put(WeatherAttribute.VISIBILITY, visibilty);
        weatherAttributes.put(WeatherAttribute.BRIGHTNESS, brightness);
        weatherAttributes.put(WeatherAttribute.DAMPNESS, dampness);
    }

    /**
     * Build the daytime transition table.
     * @return The daytime transition table.
     */
    private static Map<WeatherCond, WeatherCond[]> createDayTransitions()
    {
        Map<WeatherCond, WeatherCond[]> transitions = createSharedTransitions();
        transitions.put(Fog, new WeatherCond[] {Sunny, Fog, Rain});
        transitions.put(Cloudy, new WeatherCond[] {Sunny, Fog, Rain});
        transitions.put(Windy, new WeatherCond[] {Sunny, Fog, Rain});
        transitions.put(Storm, new WeatherCond[] {Sunny, Fog, Rain});
        return transitions;
    }

    /**
     * Build the nighttime transition table.
     * @return The nighttime transition table.
     */
    private static Map<WeatherCond, WeatherCond[]> createNightTransitions()
    {
        Map<WeatherCond, WeatherCond[]> transitions = createSharedTransitions();
        transitions.put(Fog, new WeatherCond[] {Fog, Rain});
        transitions.put(Cloudy, new WeatherCond[] {Fog, Rain});
        transitions.put(Windy, new WeatherCond[] {Fog, Rain});
        transitions.put(Storm, new WeatherCond[] {Fog, Rain});
        return transitions;
    }

    /**
     * Build the transitions that are shared by day and night.
     * @return The shared transition table.
     */
    private static Map<WeatherCond, WeatherCond[]> createSharedTransitions()
    {
        Map<WeatherCond, WeatherCond[]> transitions = new EnumMap<>(WeatherCond.class);
        transitions.put(Sunny, new WeatherCond[] {Cloudy, Windy});
        transitions.put(Rain, new WeatherCond[] {Storm, Cloudy});
        return transitions;
    }

    /**
     * Returns the next weather condition based on the current
     * weather condition and a random generator.
     * @return The next weather condition.
     */
    public WeatherCond nextCondition()
    {
        WeatherCond[] transitions = timeOfDay.isDay() ? DAY_TRANSITIONS.get(this) : NIGHT_TRANSITIONS.get(this);
        return transitions[rand.nextInt(transitions.length)];
    }

    /**
     * Returns visibility.
     * @return visibility.
     */
    public double getVisibilty()
    {
        return weatherAttributes.get(WeatherAttribute.VISIBILITY);
    }

    /**
     * Returns brightness.
     * @return brightness.
     */
    public double getBrightness()
    {
        return weatherAttributes.get(WeatherAttribute.BRIGHTNESS);
    }
    
    /**
     * Returns dampness.
     * @return dampness.
     */
    public double getDampness()
    {
        return weatherAttributes.get(WeatherAttribute.DAMPNESS);
    }
    
    /**
     * Returns the Map of all weather attributes.
     * @return The Map of all weather attributes.
     */
    public Map<WeatherAttribute, Double> getWeatherAttributesMap()
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
