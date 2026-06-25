/**
 * A system that keeps track of time and the current day.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class TimeSystem
{
    // The last recorded day:
    private static int lastRecordedDay = 0;
    
    /**
     * @return The current day.
     */
    public static int getCurrentDay()
    {
        int currentDay = (int) (Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY);
        
        return currentDay;
    }
    
    /**
     * @return Whether or not the day has changed since it was last checked.
     */
    public static boolean hasDayChanged()
    {
        if (getCurrentDay() != lastRecordedDay)
        {
            lastRecordedDay = getCurrentDay();
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * @return Whether or not it is currently night time.
     */
    public static boolean isNightTime()
    {
        // Calculate the time of day:
        double preciseCurrentDay = ((double)Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY);
        
        double timeOfDay = preciseCurrentDay - getCurrentDay(); // Between 0.0 and 1.0
        
        // If time is between 0.8 and 1.0 or 0.0 and 0.2, it is night:
        if ((timeOfDay > 0.8 && timeOfDay < 1.0) || (timeOfDay > 0.0 && timeOfDay < 0.2))
            return true;
        else
            return false;
    }
}