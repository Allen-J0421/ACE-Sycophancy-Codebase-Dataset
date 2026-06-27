/**
 * A system that keeps track of time and the current day.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class TimeSystem
{
    // The last recorded day:
    private static int lastRecordedDay = 0;
    // The fraction of the day at which night begins (dusk):
    private static final double NIGHT_START = 0.8;
    // The fraction of the day before which it is still night (dawn):
    private static final double NIGHT_END = 0.2;

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

        if (currentDay == lastRecordedDay) return false;

        lastRecordedDay = currentDay;
        return true;
    }
    
    /**
     * @return Whether or not it is currently night time.
     */
    public static boolean isNightTime()
    {
        // Calculate the time of day:
        double preciseCurrentDay = ((double)Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY);
        
        double timeOfDay = preciseCurrentDay - getCurrentDay(); // Between 0.0 and 1.0

        // It is night in the dusk window before midnight and the dawn window after:
        return (timeOfDay > NIGHT_START && timeOfDay < 1.0)
            || (timeOfDay > 0.0 && timeOfDay < NIGHT_END);
    }
}