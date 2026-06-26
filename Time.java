/**
 * A class which is used to access the step number, time, and
 * whether the simulation has finished or paused.
 *
 * @version 26/02/2022
 */
public class Time
{
    private int step = 0;
    
    // The offset of the time to start the simulation at.
    private static final int TIME_OFFSET = 12;
    
    // Stores the actual number of steps that the current
    // run of the simulation should pause at if viable.
    private int stepsToRunUntil;
    
    // Stores whether the simulation is paused or not
    private boolean isPaused = true;
    
    /**
     * Increments the step number.
     */
    public void incrementStep() 
    {
        step++;
    }
    
    /**
     * @return Returns the current step number.
     */
    public int getStep() 
    {
        return step;
    }
    
    /**
     * Resets the step number and pauses the simulation.
     */
    public void resetStep() 
    {
        step = 0;
        stepsToRunUntil = 0;
        isPaused = true;
    }
    
    /**
     * @return Returns the time in hours (between 0 and 24).
     */
    public int getTime() {
        return (step + TIME_OFFSET) % 24;
    }
    
    /**
     * @return Returns if it is currently night.
     */
    public boolean isNight() {
        int time = getTime();
        return time < 6 || time > 18;
    }
    
    /**
     * Sets the number of steps to run the simulation for.
     * @param The number of steps to run the simulation for.
     */
    public void setStepsToRunFor(int stepsToRunFor) {
        stepsToRunUntil = stepsToRunFor + getStep();
    }
    
    /**
     * @return Returns the step number to pause the current run
     * of the simualtion at.
     */
    public int getStepsToRunUntil() {
        return stepsToRunUntil;
    }
    
    /**
     * Set whether the simulation is paused or not.
     * @param The value of whether the simulation is paused or not.
     */
    public void setIsPaused(boolean newIsPaused) {
        isPaused = newIsPaused;
    }
    
    /**
     * Toggles whether the simulation is paused.
     */
    public void toggleIsPaused() {
        isPaused = ! isPaused;
    }
    
    /**
     * @return Returns if the simulation in paused.
     */
    public boolean getIsPaused() {
        return isPaused;
    }
    
    /**
     * @return Returns if the simulation has finished or not.
     */
    public boolean getIsFinished() {
        if (getStep() == getStepsToRunUntil()) {
            isPaused = true;
        }
        return isPaused;
    }
}
