
/**
 * Time class to keep track of the time in the simulation. 
 * Each step represents 3 hours.
 *
 * @version 2022.03.02
 */
public class Time
{
    //The current step the simulation is at
    private static int step;
    
    /**
     * Create a time object
     */
    public Time()
    {
        //
    }
    
    /**
     * Reset time
     */
    public void resetTime()
    {
        step = 0;
    }
    
    /**
     * Increment step
     */
    public void incrementStep(){
        step++;
    }
    
    /**
     * Return the current Hour(one step represents 3 hours)
     */
    public int getHour()
    {
        return (step%8)*3;
    }
    
    /**
     * Return the current day of the simulation
     */
    public int getDay()
    {
        return step/8;
    }
    
    /**
     * Return if the current time is day(true) or night(false)
     * @Return boolean If the time is day or night
     */
    public boolean isDay()
    {
        if (6<= getHour() && getHour() < 18){
            return true;
        }
        
        else{
            return false;
        }
    }
}

