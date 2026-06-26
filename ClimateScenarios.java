import java.util.ArrayList;

/**
 * An enum that specifies the values and actions of the different
 * climate change scenarios to approximate the real scenarios projected by the IPCC.
 *
 * @version 2022.02.28
 */
public enum ClimateScenarios
{
    SCENARIO1("none", 0,0),
    SCENARIO2("low", 1, 0.05),
    SCENARIO3("medium", 2, 0.15),
    SCENARIO4("high", 3, 0.3);
    
    private final String displayName;
    private final int initialConcreteChange;
    private double concreteChange;
    private final double changePercentage;

    /**
     * Create an appropriate Climate Scenario
     *
     * @param concreteChange (int) hold the actual temperature change value
     * @param changePercentage (double) the change percentage that is added to the concreteChange each year
     */
    ClimateScenarios(String displayName, int concreteChange, double changePercentage)
    {
        this.displayName = displayName;
        this.initialConcreteChange = concreteChange;
        this.concreteChange = concreteChange;
        this.changePercentage = changePercentage;
    }

    /**
     * @return (ArrayList<String>) the names available for the UI.
     */
    public static ArrayList<String> getScenarioNames()
    {
        ArrayList<String> scenarioNames = new ArrayList<>();
        for (ClimateScenarios scenario : values()) {
            scenarioNames.add(scenario.displayName);
        }
        return scenarioNames;
    }

    /**
     * Return the scenario matching a UI name, resetting its mutable state for a fresh simulation.
     *
     * @param scenarioName (String) the scenario display name.
     * @return (ClimateScenarios) the matching scenario, or the default scenario if no name matches.
     */
    public static ClimateScenarios fromName(String scenarioName)
    {
        for (ClimateScenarios scenario : values()) {
            if (scenario.displayName.equals(scenarioName)) {
                scenario.reset();
                return scenario;
            }
        }
        SCENARIO1.reset();
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
     * Reset this scenario to its initial temperature change.
     */
    private void reset()
    {
        concreteChange = initialConcreteChange;
    }
}
