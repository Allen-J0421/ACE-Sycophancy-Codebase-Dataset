/**
 * An enum that specifies the values and actions of the different
 * climate change scenarios to approximate the real scenarios projected by the IPCC.
 *
 * @version 2022.02.28
 */
public enum ClimateScenarios
{
    SCENARIO1(0,0), SCENARIO2(1, 0.05), SCENARIO3(2, 0.15), SCENARIO4(3, 0.3);
    
    private double concreteChange;
    private final double changePercentage;

    /**
     * Create an appropriate Climate Scenario
     *
     * @param concreteChange (int) hold the actual temperature change value
     * @param changePercentage (double) the change percentage that is added to the concreteChange each year
     */
    ClimateScenarios(int concreteChange, double changePercentage)
    {
        this.concreteChange = concreteChange;
        this.changePercentage = changePercentage;
    }
    
    /**
     * @return (int) the concreteChange as a rounded int
     */
    public int getClimateChangeEffect()
    {
        return (int) Math.round(concreteChange);
    }

    /**
     * Increases the concreteChange by the changePercentage.
     */
    public void doClimateChange()
    {
        concreteChange = concreteChange + (changePercentage * concreteChange);
    }
}
