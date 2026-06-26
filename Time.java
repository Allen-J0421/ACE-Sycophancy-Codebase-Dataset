/**
 * A class which is used to access the step number, time, and
 * whether the simulation has finished or paused.
 *
 * @version 26/02/2022
 */
public class Time
{
    private static int step = 0;

    // The offset of the time to start the simulation at.
    private static final int TIME_OFFSET = 12;

    // The step at which the current bounded run should stop (0 = no limit set).
    private static int stepsToRunUntil;

    // Stores whether the simulation is paused or not
    private static boolean isPaused = true;

    /**
     * Increments the step number.
     */
    public static void incrementStep()
    {
        step++;
    }

    /**
     * @return The current step number.
     */
    public static int getStep()
    {
        return step;
    }

    /**
     * Resets the step counter and pauses the simulation.
     */
    public static void resetStep()
    {
        step = 0;
        stepsToRunUntil = 0;
        isPaused = true;
    }

    /**
     * @return The simulation time in hours (0–23).
     */
    public static int getTime()
    {
        return (step + TIME_OFFSET) % 24;
    }

    /**
     * @return True if the current simulation time falls in the night window (before 06:00 or after 18:00).
     */
    public static boolean isNight()
    {
        int time = getTime();
        return time < 6 || time > 18;
    }

    /**
     * Schedules a stop after the given number of additional steps.
     *
     * @param stepsToRunFor The number of steps to run before stopping.
     */
    public static void setStepsToRunFor(int stepsToRunFor)
    {
        stepsToRunUntil = step + stepsToRunFor;
    }

    /**
     * @param newIsPaused Whether the simulation should be paused.
     */
    public static void setIsPaused(boolean newIsPaused)
    {
        isPaused = newIsPaused;
    }

    /**
     * Toggles the paused state.
     */
    public static void toggleIsPaused()
    {
        isPaused = !isPaused;
    }

    /**
     * @return True if the simulation is currently paused.
     */
    public static boolean getIsPaused()
    {
        return isPaused;
    }

    /**
     * @return True if a step limit was set and the current step has reached it.
     *         Returns false when no limit is active (stepsToRunUntil == 0).
     */
    public static boolean getIsFinished()
    {
        return stepsToRunUntil > 0 && step >= stepsToRunUntil;
    }
}
