/**
 * A system that keeps track of time and the current day.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class TimeSystem
{
    private static final double NIGHT_START = 0.8;
    private static final double NIGHT_END = 0.2;

    // The last recorded day:
    private static int lastRecordedDay = 0;
    
    /**
     * @return The current day.
     */
    public static int getCurrentDay()
    {
        return Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY;
    }
    
    /**
     * @return Whether or not the day has changed since it was last checked.
     */
    public static boolean hasDayChanged()
    {
        int currentDay = getCurrentDay();

        if (currentDay != lastRecordedDay)
        {
            lastRecordedDay = currentDay;
            
            return true;
        }

        return false;
    }

    /**
     * Reset time tracking to the current simulation step.
     */
    public static void reset()
    {
        lastRecordedDay = getCurrentDay();
    }
    
    /**
     * @return Whether or not it is currently night time.
     */
    public static boolean isNightTime()
    {
        double timeOfDay = getDayProgress();

        return timeOfDay >= NIGHT_START || timeOfDay < NIGHT_END;
    }

    /**
     * @return The fraction of the current day that has elapsed.
     */
    private static double getDayProgress()
    {
        double preciseCurrentDay = (double) Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY;

        return preciseCurrentDay - getCurrentDay();
    }
}
