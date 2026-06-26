/**
 * Simulates the behaviour of time by synchronizing itself with the current step of the simulation
 * (linear formula: time = k * step)
 *
 * @version 1.0
 */
public class SimulatorClock
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private static final int INITIAL_TIME = 12;
    private static final int TIME_FACTOR = 3;
    private static final int STEPS_PER_DAY = 8;
    private int step;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructor for objects of class SimulatorClock.
     */
    public SimulatorClock()
    {
        step = 0;
    }

    /**
     * Returns a string indicating the time of the day in a human friendly format.
     *
     * @return A string of the current time in a Day:Hour:Minute format.
     */
    public String getStringTime()
    {
        String hourString   = Utils.padLeft(String.valueOf(getHourOfDay()), 2);
        String minuteString = Utils.padRight(String.valueOf(getMinuteOfDay()), 2);
        return String.format("Day: %s | %s : %s ", getDayCount(), hourString, minuteString);
    }

    /*///////////////////////////////////////////////////////////////
                          TIME SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Accessor method for the current time (starts at mid day).
     *
     * @return the current time in hours, in the range [0, 24).
     */
    public double getTime()
    {
        return ((double)(TIME_FACTOR * step) + INITIAL_TIME) % 24;
    }

    /**
     * Accessor method for the current hour of the day.
     *
     * @return the current hour (floored).
     */
    public int getHourOfDay()
    {
        return (int) getTime();
    }

    /**
     * The remainder of the current hours.
     *
     * @return the minutes passed since the start of the last hour.
     */
    public int getMinuteOfDay()
    {
        return (int)((getTime() - getHourOfDay()) * 60);
    }

    /**
     * Increments the internal steps of the clock.
     */
    public void incrementStep()
    {
        step++;
    }

    /**
     * Returns the number of days passed since the start of the simulation (starts at day 1).
     *
     * @return the current day of the simulation.
     */
    public int getDayCount()
    {
        return ((step + (INITIAL_TIME / TIME_FACTOR)) / STEPS_PER_DAY) + 1;
    }

    /**
     * Returns the current state of the day.
     *
     * @return the state of the day.
     */
    public DayState getDayState()
    {
        if(getTime() < 6) {
            return DayState.NIGHT;
        } else {
            return DayState.DAYLIGHT;
        }
    }
}
