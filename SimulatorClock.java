/**
 * Simulates the behaviour of time by synchronizing itself with the current step of the simulation.
 * Delegates all time calculation and state-transition logic to TimeHandler.
 *
 * @version 1.0
 */
public class SimulatorClock
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private final TimeHandler timeHandler;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructor for objects of class SimulatorClock.
     */
    public SimulatorClock()
    {
        timeHandler = new TimeHandler();
    }

    /*///////////////////////////////////////////////////////////////
                          TIME SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Increments the internal steps of the clock.
     */
    public void incrementStep()
    {
        timeHandler.incrementStep();
    }

    /**
     * Returns the number of days passed since the start of the simulation (starts at day 1).
     *
     * @return the current day of the simulation.
     */
    public int getDayCount()
    {
        return timeHandler.getDayCount();
    }

    /**
     * Returns the current state of the day.
     *
     * @return the state of the day.
     */
    public DayState getDayState()
    {
        return timeHandler.getDayState();
    }

    /**
     * Returns a string indicating the time of the day in a human friendly format.
     *
     * @return a string of the current time in Day:Hour:Minute format.
     */
    public String getStringTime()
    {
        return timeHandler.getStringTime();
    }
}
