/**
 * Keep track of the time in the simulation
 * and specify if it is day or night
 *
 * @version 2022.02.24
 */
public class Time
{
    // true if it is night
    private boolean isNight;
    // keep track of the simulation steps.
    private SimulationStep simStep;
    // keep track of the hour of the day
    private int currentHour;
    // the number of steps before the type of day changes
    private static final int DAY_CHANGE = 1;

    /**
     * Construct a Time object with the given parameters.
     *
     * @param simStep (SimulationStep) The object of SimulationStep to use for keeping track of the steps.
     * @param startNight (boolean) true if first iteration is night, false if day.
     */
    public Time(SimulationStep simStep, boolean startNight)
    {
        this.simStep = simStep;
        isNight = startNight;

        if(isNight) {
            currentHour = 18;
        }
        else {
            currentHour = 6;
        }
    }

    /**
     * @return (boolean) true if night, false otherwise.
     */
    public boolean getIsNight()
    {
        return isNight;
    }

    /**
     * Create a String indicating night or day and the specific hour.
     *
     * @return (String) A String with the time's full information.
     */
    public String timeString()
    {
        String tString = "";

        if(isNight) {
            tString += "Night \t";
        }
        else {
            tString += "Day \t";
        }

        tString += "(" + getHourDisplay() + ")";

        return tString;
    }

    /**
     * Change the day status if 'DAY_CHANGE' steps have passed, and increment the hours.
     */
    public void timeStep()
    {
        int step = simStep.getCurrentStep();

        if(step != 0 && (step+1) % DAY_CHANGE == 0) { // step+1 because step starts with 0
            toggleIsNight();
        }

        incHour();
    }

    /**
     * Change the current status of the time.
     */
    private void toggleIsNight()
    {
        isNight = ! isNight;
    }

    /**
     * Increment the hours to go through the day evenly.
     */
    private void incHour()
    {
        currentHour = (currentHour + (24 / (DAY_CHANGE * 2))) % 24;
    }

    /**
     * Create a string displaying time in 24-hour style.
     *
     * @return (String)a String holding the current time.
     */
    private String getHourDisplay()
    {
        if(currentHour < 10)
        {
            return "0" + currentHour + ":00";
        }

        return currentHour + ":00";
    }
}
