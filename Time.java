package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A class which is used to access the step number, time, and
 * whether the simulation has finished or paused.
 *
 * @version 26/02/2022
 */
public class Time
{
    private static final SimulationConfig CONFIG = SimulationConfig.DEFAULT;
    private static int step = 0;
    
    // The offset of the time to start the simulation at.
    private static final int TIME_OFFSET = CONFIG.timeOffset;
    
    // Stores the actual number of steps that the current
    // run of the simulation should pause at if viable.
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
     * @return Returns the current step number.
     */
    public static int getStep() 
    {
        return step;
    }
    
    /**
     * Resets the step number and pauses the simulation.
     */
    public static void resetStep() 
    {
        step = 0;
        stepsToRunUntil = 0;
        isPaused = true;
    }
    
    /**
     * @return Returns the time in hours (between 0 and 24).
     */
    public static int getTime() {
        int time = (step + TIME_OFFSET)%CONFIG.hoursPerDay;
        return time;
    }
    
    /**
     * @return Returns if it is currently night.
     */
    public static boolean isNight() {
        int time = getTime();
        return time < CONFIG.dayStartHour || time > CONFIG.nightStartHour;
    }
    
    /**
     * Sets the number of steps to run the simulation for.
     * @param The number of steps to run the simulation for.
     */
    public static void setStepsToRunFor(int stepsToRunFor) {
        stepsToRunUntil = stepsToRunFor + Time.getStep();
    }
    
    /**
     * @return Returns the step number to pause the current run
     * of the simualtion at.
     */
    public static int getStepsToRunUntil() {
        return stepsToRunUntil;
    }
    
    /**
     * Set whether the simulation is paused or not.
     * @param The value of whether the simulation is paused or not.
     */
    public static void setIsPaused(boolean newIsPaused) {
        isPaused = newIsPaused;
    }
    
    /**
     * Toggles whether the simulation is paused.
     */
    public static void toggleIsPaused() {
        isPaused = ! isPaused;
    }
    
    /**
     * @return Returns if the simulation in paused.
     */
    public static boolean getIsPaused() {
        return isPaused;
    }
    
    /**
     * @return Returns if the simulation has finished or not.
     */
    public static boolean getIsFinished() {
        if (getStep() == getStepsToRunUntil()) {
            isPaused = true;
        }
        return isPaused;
    }
}
