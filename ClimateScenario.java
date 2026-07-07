/**
 * Holds the state and behaviour of a single climate change scenario.
 * Instances are created by ClimateScenarioFactory from scenarios.csv data,
 * replacing the hardcoded values that previously lived in the ClimateScenarios enum.
 *
 * @version 2022.03.01
 */
public class ClimateScenario
{
    // The current temperature offset applied by this scenario.
    private double concreteChange;
    // The annual compounding rate of the offset.
    private final double changePercentage;

    /**
     * Create a climate scenario with the given parameters.
     *
     * @param concreteChange (double) Initial temperature offset.
     * @param changePercentage (double) Annual compounding rate applied to the offset.
     */
    public ClimateScenario(double concreteChange, double changePercentage)
    {
        this.concreteChange = concreteChange;
        this.changePercentage = changePercentage;
    }

    /**
     * @return (int) The current climate change effect as a rounded integer.
     */
    public int getClimateChangeEffect()
    {
        return (int) Math.round(concreteChange);
    }

    /**
     * Compound the temperature offset by one year's growth rate.
     */
    public void doClimateChange()
    {
        concreteChange = concreteChange + (changePercentage * concreteChange);
    }
}
