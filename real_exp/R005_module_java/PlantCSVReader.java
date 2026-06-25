/**
 * This class reads from "plants.csv" the information about all the plants
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */

public class PlantCSVReader extends CSVReader
{
    // Name of the file containing plant data.
    private static final String FILE_NAME = "plants.csv";
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
    // Tool to alert user about any potential error.
    private ErrorThrower errorThrower;
    
    /**
     * Build an PlantCSVReader and initializes field.
     */
    public PlantCSVReader ()
    {
        errorThrower = new ErrorThrower();
        name = null;
        maximumTemperature = 0;
        minimumTemperature = 0;
        nutritionalValue = 0;
        reproductionProbability = 0;
        maxHealth = 0;
    }

    /**
     * Populated fields with the data read from the files. This method overrides a method of the CSVReader parent class
     * and is therefore called after reading the data.
     *
     * @param extractedData (String[]) The data read.
     */
    protected void populateFields(String[] extractedData)
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
     * Set all parameters back to initial values before reading data for another plant.
     */
    protected void resetParameters()
    {
        name = null;
        maximumTemperature = 0;
        minimumTemperature = 0;
        nutritionalValue = 0;
        reproductionProbability = 0;
        maxHealth =0;
    }

    /**
     * @return (String) The name of the file containing plant data.
     */
    protected String getFileName() {
        return FILE_NAME;
    }

    /**
     * @return (int) The plant's nutritional value.
     */
    public int getNutritionalValue() {
        return nutritionalValue;
    }

    /**
     * @return (int) The minimum temperature a plant can survive to.
     */
    public int getMinimumTemperature() {
        return minimumTemperature;
    }

    /**
     * @return (int) The maximum temperature a plant can survive to.
     */
    public int getMaximumTemperature() {
        return maximumTemperature;
    }

    /**
     * @return (String) The plant's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return (double) Probability that the plant reproduces.
     */
    public double getReproductionProbability() {
        return reproductionProbability;
    }

    /**
     * @return (int) The plant's maximum health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }
}