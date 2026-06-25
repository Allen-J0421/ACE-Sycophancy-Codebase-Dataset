import java.util.Random;
/**
 * A class to model weather conditions in the environment
 *
 * @version 2022.25.02
 */
public class Weather
{
    
    private boolean rain;
    private boolean fog;
    private boolean thunder;
    private int daysNotRained;
    private int hoursThatHaveRain;
    
    // The probability that it will rain
    private static final double RAIN_CREATION_PROBABILITY = 0.25;
    // The probability that it will fog
    private static final double FOG_CREATION_PROBABILITY = 0.1;
    // The probability that there will be lightning
    private static final double THUNDER_CREATION_PROBABILITY = 0.15;


    /**
     * Initialises the default settings to change the weather of the environment
     */
    public Weather()
    {
        rain = false;
        fog = false;
        thunder = false;
        daysNotRained = 0;
        hoursThatHaveRain = 0;
    }
    
    /**
     * Make the weather act - that is: make it do
     * whatever it wants/needs to do.
     * @field The field currently occupied
     * @natureField The nature field currently occupied
     */
    public void weatherAct(Field field, NatureField natureField)
    {
        Random rand = Randomizer.getRandom();
        
        resetWeather();
        
        if (rand.nextDouble() <= RAIN_CREATION_PROBABILITY) {
            addRain();
            hoursThatHaveRain = 0;
        }
        else {
            if (hoursThatHaveRain >= 6) {
                daysNotRained ++;
                hoursThatHaveRain = 0;
            }
            
            if (rand.nextDouble() <= FOG_CREATION_PROBABILITY) {
                addFog();
                hoursThatHaveRain ++;
            }
            else if(rand.nextDouble() <= THUNDER_CREATION_PROBABILITY) {
                addThunder();
                thunderAct(field, natureField);
                hoursThatHaveRain ++;
            }
        }
        
    }
    
    /**
     * Strikes lightning on 10 spots on the field killing
     * everything present on that spot
     * @field The field currently occupied
     * @natureField The nature field currently occupied
     */
    private void thunderAct(Field field, NatureField natureField) {
        Random rand = Randomizer.getRandom();
        
        for (int i = 0; i < 10; i++ ) {
            int row = rand.nextInt(field.getDepth());
            int col = rand.nextInt(field.getWidth());
            Location location = new Location(row, col);
            field.clear(location);
            natureField.clear(location);
            
        }
    }
    
    /**
     * Returns the consecutive amount of days not rained for
     * @return The consecutive amount of days not rained for
     */
    public int getDaysNotRained()
    {
        return daysNotRained;
    }

    /**
     * Adds rain to the environment
     */
    private void addRain()
    {
        rain = true;
        daysNotRained = 0;
    }

    /**
     * Adds fog to the environment
     */
    private void addFog()
    {
        fog = true;
    }
    
    /**
     * Adds thunder to the environment
     */
    private void addThunder()
    {
        thunder = true;
    }
    
    /**
     * Returns the status of rain in the environment
     * @return status of rain in the environment
     */
    public boolean isRaining() 
    {
        return rain;
    }

    /**
     * Returns the status of fog in the environment
     * @return status of fog in the environment
     */
    public boolean isFogging()
    {
        return fog;
    }
    
    /**
     * Returns the status of thunder in the environment
     * @return status of thunder in the environment
     */
    public boolean isThunder()
    {
        return thunder;
    }
    
    /**
     * Resets the weather (i.e. make it a clear blue sky)
     */
    private void resetWeather()
    {
        rain = false;
        fog = false;
        thunder = false;
    }
    
    /**
     * Return the information of the weather.
     * 
     * @return The weather information.
     */
    public String getWeatherInformation()
    {
        if(rain) {
            return " Rain";
        }
        else if(fog) {
            return " Fog";
        }
        else if(thunder) {
            return " Thunder";
        }
        return " Clear Weather";
    }
}
