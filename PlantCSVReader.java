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
    // Parsed plant profile from the latest CSV extraction.
    private PlantProfile plantProfile;
    // Tool to alert user about any potential error.
    private ErrorThrower errorThrower;
    
    /**
     * Build an PlantCSVReader and initializes field.
     */
    public PlantCSVReader ()
    {
        errorThrower = new ErrorThrower();
        resetParameters();
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
        plantProfile = new PlantProfile(
            extractedData[0],
            Integer.valueOf(extractedData[1]),
            Integer.valueOf(extractedData[2]),
            Integer.valueOf(extractedData[3]),
            Double.valueOf(extractedData[4]),
            Integer.valueOf(extractedData[5])
        );
    }

    /**
     * Set all parameters back to initial values before reading data for another plant.
     */
    protected void resetParameters()
    {
        plantProfile = new PlantProfile(null, 0, 0, 0, 0, 0);
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
        return plantProfile.getNutritionalValue();
    }

    /**
     * @return (int) The minimum temperature a plant can survive to.
     */
    public int getMinimumTemperature() {
        return plantProfile.getMinimumTemperature();
    }

    /**
     * @return (int) The maximum temperature a plant can survive to.
     */
    public int getMaximumTemperature() {
        return plantProfile.getMaximumTemperature();
    }

    /**
     * @return (String) The plant's name.
     */
    public String getName() {
        return plantProfile.getName();
    }

    /**
     * @return (double) Probability that the plant reproduces.
     */
    public double getReproductionProbability() {
        return plantProfile.getReproductionProbability();
    }

    /**
     * @return (int) The plant's maximum health.
     */
    public int getMaxHealth() {
        return plantProfile.getMaxHealth();
    }

    /**
     * @return (PlantProfile) The immutable profile represented by the last extracted CSV row.
     */
    public PlantProfile getPlantProfile()
    {
        return plantProfile;
    }
}
