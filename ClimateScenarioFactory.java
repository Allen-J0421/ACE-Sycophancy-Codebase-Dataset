/**
 * Reads climate scenario configuration from scenarios.csv and creates
 * ClimateScenario instances on demand. Extends CSVReader to follow the
 * same data-loading pattern used by AnimalCSVReader, PlantCSVReader, and
 * HabitatCSVReader. The inherited getChoicesList() method also serves the GUI
 * with the list of available scenario names.
 *
 * @version 2022.03.01
 */
public class ClimateScenarioFactory extends CSVReader
{
    private static final String FILE_NAME = "scenarios.csv";
    // Tool to alert user about any potential error.
    private final ErrorThrower errorThrower;
    // Initial temperature offset read from CSV.
    private double concreteChange;
    // Annual compounding rate read from CSV.
    private double changePercentage;

    /**
     * Build a ClimateScenarioFactory and initialise its fields.
     */
    public ClimateScenarioFactory()
    {
        errorThrower = new ErrorThrower();
        concreteChange = 0;
        changePercentage = 0;
    }

    /**
     * Populate fields with data read from the CSV row.
     * Expected columns: name, concreteChange, changePercentage.
     *
     * @param extractedData (String[]) The raw CSV row.
     */
    protected void populateFields(String[] extractedData)
    {
        if (extractedData.length != 3) {
            errorThrower.throwMessage("Scenarios .csv issue, please restart.");
        }
        concreteChange = Double.parseDouble(extractedData[1]);
        changePercentage = Double.parseDouble(extractedData[2]);
    }

    /**
     * Reset all fields before reading data for another scenario.
     */
    protected void resetParameters()
    {
        concreteChange = 0;
        changePercentage = 0;
    }

    /**
     * @return (String) The name of the file containing scenario data.
     */
    protected String getFileName()
    {
        return FILE_NAME;
    }

    /**
     * Look up the named scenario in scenarios.csv and return a configured
     * ClimateScenario instance.
     *
     * @param scenarioName (String) The scenario name (e.g. "none", "low").
     * @return (ClimateScenario) A new ClimateScenario populated from CSV data.
     */
    public ClimateScenario createScenario(String scenarioName)
    {
        extractDataFor(scenarioName);
        return new ClimateScenario(concreteChange, changePercentage);
    }
}
