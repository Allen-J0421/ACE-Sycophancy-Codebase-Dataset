import java.util.ArrayList;

/**
 * This class reads from "plants.csv" the information about all the plants
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */
public class PlantCSVReader
{
    // Name of the file containing plant data.
    private static final String FILE_NAME = "plants.csv";
    // Centralised CSV file I/O.
    private final ConfigurationLoader loader;
    // Tool to alert user about any potential error.
    private final ErrorThrower errorThrower;

    // The plant's name.
    private String name;
    // The maximum temperature plant can survive to.
    private int maximumTemperature;
    // The minimum temperature plant can survive to.
    private int minimumTemperature;
    // The nutritional value brought when plant is eaten.
    private int nutritionalValue;
    // Probability to see plant reproduce.
    private double reproductionProbability;
    // The plant's maximum health.
    private int maxHealth;

    /**
     * Build a PlantCSVReader and initialise its fields.
     */
    public PlantCSVReader()
    {
        loader = new ConfigurationLoader();
        errorThrower = new ErrorThrower();
        resetParameters();
    }

    /**
     * Load and parse the row for the named plant from the CSV file.
     *
     * @param plantName (String) The plant name to look up.
     */
    public void extractDataFor(String plantName)
    {
        resetParameters();
        String[] data = loader.loadRow(FILE_NAME, plantName);
        if (data != null) {
            populateFields(data);
        } else {
            System.out.println("ERROR: no data could be read for " + plantName);
        }
    }

    /**
     * Return the list of plant names available in the CSV file.
     *
     * @return (ArrayList<String>) All plant names.
     */
    public ArrayList<String> getChoicesList()
    {
        return loader.loadNames(FILE_NAME);
    }

    /**
     * Parse a raw CSV row into the typed fields of this reader.
     *
     * @param extractedData (String[]) The raw CSV row.
     */
    private void populateFields(String[] extractedData)
    {
        if (extractedData.length != 6) {
            errorThrower.throwMessage("Plant .csv issue, please restart.");
        }
        name = extractedData[0];
        maximumTemperature = Integer.valueOf(extractedData[1]);
        minimumTemperature = Integer.valueOf(extractedData[2]);
        nutritionalValue = Integer.valueOf(extractedData[3]);
        reproductionProbability = Double.valueOf(extractedData[4]);
        maxHealth = Integer.valueOf(extractedData[5]);
    }

    /**
     * Reset all fields to initial values before reading data for another plant.
     */
    private void resetParameters()
    {
        name = null;
        maximumTemperature = 0;
        minimumTemperature = 0;
        nutritionalValue = 0;
        reproductionProbability = 0;
        maxHealth = 0;
    }

    /** @return (int) The plant's nutritional value. */
    public int getNutritionalValue() { return nutritionalValue; }

    /** @return (int) The minimum temperature a plant can survive to. */
    public int getMinimumTemperature() { return minimumTemperature; }

    /** @return (int) The maximum temperature a plant can survive to. */
    public int getMaximumTemperature() { return maximumTemperature; }

    /** @return (String) The plant's name. */
    public String getName() { return name; }

    /** @return (double) Probability that the plant reproduces. */
    public double getReproductionProbability() { return reproductionProbability; }

    /** @return (int) The plant's maximum health. */
    public int getMaxHealth() { return maxHealth; }
}
