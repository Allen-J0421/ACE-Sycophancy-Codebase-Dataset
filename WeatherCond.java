import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
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
    private Map<String, Double> weatherAttributes; 
    private static Time timeOfDay;

    /**
     * Puts attributes of each weather condition in a Map.
     * @param visibility The visibility that the weather condition has.
     * @param brightness The brightness that the weather condition has.
     * @param dampness The dampness that the weather condition has.
     */
    WeatherCond(double visibility, double brightness, double dampness)
    {
        weatherAttributes = new HashMap<>();
        weatherAttributes.put("visibility", visibility);
        weatherAttributes.put("brightness", brightness);
        weatherAttributes.put("dampness", dampness);
    }

    /**
     * Returns the next weather condition based on the current
     * weather condition and a random generator.
     * @return The next weather condition.
     */
    public WeatherCond nextCondition()
    {
        List<WeatherCond> tempList = new ArrayList<>();
        switch(this) {
            case Sunny:
                tempList.add(Cloudy);
                tempList.add(Windy);
                break;
            case Rain:
                tempList.add(Storm);
                tempList.add(Cloudy);
                break;
            default:
                if(timeOfDay.isDay()) { tempList.add(Sunny); }
                tempList.add(Fog);
                tempList.add(Rain);
                break;
        }
        return tempList.get(rand.nextInt((tempList.size())));
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
        return Collections.unmodifiableMap(weatherAttributes);
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

