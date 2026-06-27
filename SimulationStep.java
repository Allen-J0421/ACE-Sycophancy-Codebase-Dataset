/**
 * A simple class to keep track of and increment
 * the steps in the simulation.
 *
 * @version 2022.02.16
 */
public class SimulationStep
{
    private int currentStep;

    /**
     * Construct a SimulationStep object starting with step 0.
     */
    public SimulationStep()
    {
        currentStep = 0;
    }

    /**
     * @return (int) The current step.
     */
    public int getCurrentStep()
    {
        return currentStep;
    }

    /**
     * Increment the steps by one.
     */
    public void incStep()
    {
        currentStep++;
    }

    /**
     * Reset the current step to 0.
     */
    public void reset()
    {
        currentStep = 0;
    }
}
