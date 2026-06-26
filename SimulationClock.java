/**
 * Tracks simulation step, hour, day, and time-of-day progression.
 */
public class SimulationClock {

    private static final int STEPS_PER_HOUR = 5;
    private static final int HOURS_PER_DAY = 24;
    private static final int STEPS_PER_DAY = STEPS_PER_HOUR * HOURS_PER_DAY;
    private static final int HOURS_PER_TIME_OF_DAY = 4;

    private int step;
    private int hour;
    private TimeOfDay currentTime;

    /**
     * Constructor for a simulation clock.
     */
    public SimulationClock() {
        reset();
    }

    /**
     * Reset the clock to the initial simulation time.
     */
    public void reset() {
        step = 0;
        hour = 0;
        currentTime = TimeOfDay.SUNRISE;
    }

    /**
     * Advance the simulation by one step.
     */
    public void advanceStep() {
        step++;
        hour = (step / STEPS_PER_HOUR) % HOURS_PER_DAY + 1;
    }

    /**
     * Advance time of day when the current step reaches a transition boundary.
     */
    public void advanceTimeOfDayIfNeeded() {
        if ((hour % HOURS_PER_TIME_OF_DAY == 0) && isStartOfHour()) {
            currentTime = currentTime.next();
        }
    }

    /**
     * Whether this step begins a new simulated hour.
     *
     * @return true if the step begins a new simulated hour.
     */
    public boolean isStartOfHour() {
        return step % STEPS_PER_HOUR == 0;
    }

    /**
     * Return the current simulation step.
     *
     * @return The current step.
     */
    public int getStep() {
        return step;
    }

    /**
     * Return the current simulation day.
     *
     * @return The current day.
     */
    public int getDay() {
        return step / STEPS_PER_DAY + 1;
    }

    /**
     * Return the current simulation hour.
     *
     * @return The current hour.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Return the current time of day.
     *
     * @return The current time of day.
     */
    public TimeOfDay getCurrentTime() {
        return currentTime;
    }
}
