import java.util.ArrayList;

/**
 * Reads climate scenario configuration from scenarios.csv and creates
 * ClimateScenario instances on demand. Uses ConfigurationLoader for file I/O
 * rather than inheriting from CSVReader.
 *
 * @version 2022.03.01
 */
public class ClimateScenarioFactory
{
    private static final String FILE_NAME = "scenarios.csv";
    // Centralised CSV file I/O.
    private final ConfigurationLoader loader;
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
        loader = new ConfigurationLoader();
        errorThrower = new ErrorThrower();
        resetParameters();
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
        resetParameters();
        String[] data = loader.loadRow(FILE_NAME, scenarioName);
        if (data != null) {
            populateFields(data);
        } else {
            System.out.println("ERROR: no data could be read for " + scenarioName);
        }
        return new ClimateScenario(concreteChange, changePercentage);
    }

    /**
     * Return the list of scenario names available in the CSV file.
     * Used by the GUI to populate the scenario choice list.
     *
     * @return (ArrayList<String>) All scenario names.
     */
    public ArrayList<String> getChoicesList()
    {
        return loader.loadNames(FILE_NAME);
    }

    /**
     * Parse a raw CSV row into the typed fields of this factory.
     * Expected columns: name, concreteChange, changePercentage.
     *
     * @param extractedData (String[]) The raw CSV row.
     */
    private void populateFields(String[] extractedData)
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
    private void resetParameters()
    {
        concreteChange = 0;
        changePercentage = 0;
    }
}
