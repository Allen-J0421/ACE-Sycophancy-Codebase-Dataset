/**
 * Time class that deals with the time that is passed for each step taken 
 * Used for the day and night cycle
 *
 * @version 27.02.22
 */
public class Time
{
    private static final int DAY_START = 6;
    private static final int DAY_END = 18;

    private int time;
    // How much time will increment by for each step
    private final int timeStep;

    /**
     * Creates the time, sets it to 0 and takes how much time will increment by
     * @param timeStep How much time will increment by for each step
     */
    public Time(int timeStep)
    {
        time = 0;
        this.timeStep = timeStep;
    }

    /**
     * Used to increment the time by the time step, keeps it at range 0-24
     * to simulate a 24hr time system
     */
    public void incrementTime()
    {
        time = (time + timeStep) % 24;
    }

    /**
     * Used to check if it is day which is true or if its night
     * @return true if day, false if it is night
     */
    public boolean isDay(){
        return time > DAY_START && time < DAY_END;
    }
}
