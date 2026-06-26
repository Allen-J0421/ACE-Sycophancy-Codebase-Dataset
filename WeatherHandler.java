/**
 * Responsible for mutating the weather of the simulation by randomly selecting a new weather(following a uniform distribution)
 * @version 1.0
 */
public class WeatherHandler
{
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private Weather currentWeather;
    private int lastRecordedDay;
    private final SimulatorClock clock;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a new Weather handler
     */
    public WeatherHandler(SimulatorClock clock)
    {
        currentWeather = getRandomWeather();
        lastRecordedDay = 1;
        this.clock = clock;
    }
    
    /**
     * Accessor Method for the current weathet
     * @return the current weather
     */
    public Weather getWeather()
    {
        return currentWeather;
    }
    
    /**
     * Randomly selects a new weather everyday
     */
    public void updateWeather()
    {
        if(lastRecordedDay != clock.getDayCount()) {
            currentWeather = getRandomWeather();
            lastRecordedDay = clock.getDayCount();
        }
    }
    
    /**
     *  Returns a random weather amongs the list of possible types of weather
     *  @return the randomly selected weather
     */
    private Weather getRandomWeather()
    {
        return Utils.getRandomEnumValue(Weather.class);
    }
}
