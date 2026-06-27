/**
 * An enum that specifies the values and actions of the different
 * climate change scenarios to approximate the real scenarios projected by the IPCC.
 *
 * @version 2022.02.28
 */
public enum ClimateScenarios
{
    SCENARIO1("none", 0, 0), SCENARIO2("low", 1, 0.05), SCENARIO3("medium", 2, 0.15), SCENARIO4("high", 3, 0.3);

    private final String label;
    private double concreteChange;
    private final double initialConcreteChange;
    private final double changePercentage;

    /**
     * Create an appropriate Climate Scenario
     *
     * @param label (String) the user-facing name for this scenario
     * @param concreteChange (int) hold the actual temperature change value
     * @param changePercentage (double) the change percentage that is added to the concreteChange each year
     */
    ClimateScenarios(String label, int concreteChange, double changePercentage)
    {
        this.label = label;
        this.concreteChange = concreteChange;
        this.initialConcreteChange = concreteChange;
        this.changePercentage = changePercentage;
    }

    /**
     * @return (String) the user-facing label for this scenario
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Return the scenario whose label matches the given string, defaulting to SCENARIO1 (no change).
     *
     * @param label (String) the label to look up
     * @return (ClimateScenarios) the matching scenario
     */
    public static ClimateScenarios fromLabel(String label)
    {
        for (ClimateScenarios s : values()) {
            if (s.label.equals(label)) return s;
        }
        return SCENARIO1;
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

    /**
     * Reset concreteChange to its initial value so each simulation run starts from the base scenario.
     */
    public void reset()
    {
        concreteChange = initialConcreteChange;
    }
}
