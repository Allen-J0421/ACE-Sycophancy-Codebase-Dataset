/**
 * Encapsulates all time-tracking and time-calculation logic for the simulation.
 * Converts a linear step counter into human-readable time, day counts, and day/night state.
 *
 * @version 1.0
 */
public class TimeHandler
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    private final int INITIAL_TIME = 12;
    private final int TIME_FACTOR = 3;
    private final int STEPS_PER_DAY = 8;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private int step;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructs a TimeHandler starting at step 0.
     */
    public TimeHandler()
    {
        step = 0;
    }

    /*///////////////////////////////////////////////////////////////
                          TIME SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Increments the internal step counter.
     */
    public void incrementStep()
    {
        step++;
    }

    /**
     * Returns the current time of day as a fractional hour (0–24).
     *
     * @return the current time.
     */
    public double getTime()
    {
        return (((double)(TIME_FACTOR) * step) + INITIAL_TIME) % 24;
    }

    /**
     * Returns the current hour of the day (floored).
     *
     * @return the current hour.
     */
    public int getHourOfDay()
    {
        return (int) getTime();
    }

    /**
     * Returns the minutes elapsed since the start of the current hour.
     *
     * @return the current minute.
     */
    public int getMinuteOfDay()
    {
        return (int)((getTime() - (double)getHourOfDay()) * 100.0 * 0.6);
    }

    /**
     * Returns the number of days elapsed since the start of the simulation (starts at 1).
     *
     * @return the current day count.
     */
    public int getDayCount()
    {
        return ((int)((step + (INITIAL_TIME / TIME_FACTOR)) / STEPS_PER_DAY) + 1);
    }

    /**
     * Returns the current state of the day.
     *
     * @return NIGHT or DAYLIGHT based on the current time.
     */
    public DayState getDayState()
    {
        if(getTime() > 24 || getTime() < 6) {
            return DayState.NIGHT;
        } else {
            return DayState.DAYLIGHT;
        }
    }

    /**
     * Returns a human-readable string representation of the current time.
     *
     * @return a Day:Hour:Minute formatted string.
     */
    public String getStringTime()
    {
        String hourString = Utils.padLeft(String.valueOf(getHourOfDay()), 2);
        String minuteString = Utils.padRight(String.valueOf(getMinuteOfDay()), 2);
        return String.format("Day: %s | %s : %s ", getDayCount(), hourString, minuteString);
    }
}
