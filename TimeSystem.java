/**
 * A system that keeps track of time and the current day.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class TimeSystem
{
    // Fraction of the day (0.0–1.0) at which night begins and ends:
    private static final double NIGHT_START = 0.8;
    private static final double NIGHT_END   = 0.2;

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
     * @return Whether or not it is currently night time.
     */
    public static boolean isNightTime()
    {
        double preciseCurrentDay = (double) Simulator.getCurrentStep() / Simulator.NUMBER_OF_STEPS_PER_DAY;
        double timeOfDay = preciseCurrentDay - getCurrentDay(); // Between 0.0 and 1.0

        // Night is the last NIGHT_START fraction and the first NIGHT_END fraction of each day:
        return (timeOfDay > NIGHT_START && timeOfDay < 1.0)
            || (timeOfDay > 0.0         && timeOfDay < NIGHT_END);
    }
}
