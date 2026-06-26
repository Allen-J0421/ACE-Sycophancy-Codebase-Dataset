import java.util.Arrays;

/**
 * This class reads from "habitats.csv" the information about all the habitats
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */

public class HabitatCSVReader extends CSVReader {
    private static final int EXPECTED_ATTRIBUTE_COUNT = 9;
    private static final int TEMPERATURE_VALUES_PER_SEASON = 2;
    // List of minimum, average, and maximum temperatures for the winter.
    private final int[] winterTemperatures;
    // List of minimum, average, and maximum temperatures for the spring.
    private final int[] springTemperatures;
    // List of minimum, average, and maximum temperatures for the summer.
    private final int[] summerTemperatures;
    // List of minimum, average, and maximum temperatures for the autumn.
    private final int[] autumnTemperatures;
    // The concentration of plants in a given habitat.
    private double plantConcentration;

    // Name of the CSV files containing data on fields.
    private static final String FILE_NAME = "habitats.csv";

    /**
     * Build a HabitatCSVReader and initialize its fields.
     */
    public HabitatCSVReader()
    {
        winterTemperatures = new int[TEMPERATURE_VALUES_PER_SEASON];
        autumnTemperatures = new int[TEMPERATURE_VALUES_PER_SEASON];
        springTemperatures = new int[TEMPERATURE_VALUES_PER_SEASON];
        summerTemperatures = new int[TEMPERATURE_VALUES_PER_SEASON];
        resetParameters();
    }

    /**
     * Populate the fields with the data extracted from the .csv file. Fields are used to store data of the right type to be easily fetched
     * by the Initializer when trying to get information about a specific habitat.
     * Habitat name from the data is removed to facilitate reading, the first 12 elements are in groups of 2 relating to each season
     * (average temperature, and maximum change to temperature ), the 13th element is the plant concentration in that habitat.
     *
     * @param extractedData (String[]) the data from the CSV file relative to a specific habitat.
     */
    protected void populateFields(String[] extractedData)
    {
        extractedData = removeHabitatName(extractedData);
        if (!hasExpectedAttributeCount(extractedData, EXPECTED_ATTRIBUTE_COUNT, "Habitat issue, please restart.")) {
            return;
        }

        populateTemperaturesForSeason(winterTemperatures, extractedData, 0);
        populateTemperaturesForSeason(springTemperatures, extractedData, TEMPERATURE_VALUES_PER_SEASON);
        populateTemperaturesForSeason(summerTemperatures, extractedData, TEMPERATURE_VALUES_PER_SEASON * 2);
        populateTemperaturesForSeason(autumnTemperatures, extractedData, TEMPERATURE_VALUES_PER_SEASON * 3);
        plantConcentration = Double.parseDouble(extractedData[EXPECTED_ATTRIBUTE_COUNT - 1]);
    }

    /**
     * Set all parameters back to initial values before reading data for another habitat.
     */
    protected void resetParameters()
    {
        Arrays.fill(winterTemperatures, 0);
        Arrays.fill(springTemperatures, 0);
        Arrays.fill(summerTemperatures, 0);
        Arrays.fill(autumnTemperatures, 0);
        plantConcentration = 0;
    }

    /**
     * Returns the list of extracted data without the habitat's name (as this informaiton is known by the Initializer and will therefore
     * not be asked). The name is the first element of the attributes list.
     *
     * @param attributes (String[]) the data from the CSV file relative to a specific habitat.
     * @return (String[]) The data without the habitat's name.
     */
    private String[] removeHabitatName(String[] attributes)
    {
        String[] attributesWithoutHabitatName = Arrays.copyOfRange(attributes,1,attributes.length);
        return attributesWithoutHabitatName;
    }

    private void populateTemperaturesForSeason(int[] seasonTemperatures, String[] extractedData, int startIndex)
    {
        for (int i = 0; i < TEMPERATURE_VALUES_PER_SEASON; i++) {
            seasonTemperatures[i] = Integer.parseInt(extractedData[startIndex + i]);
        }
    }

    /**
     * @return (String) The name of the file containing habitat data.
     */
    protected String getFileName() {
        return FILE_NAME;
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the winter.
     */
    public int[] getWinterTemperatures()
    {
        return copyTemperatures(winterTemperatures);
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the autumn.
     */
    public int[] getAutumnTemperatures() {
        return copyTemperatures(autumnTemperatures);
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the spring.
     */
    public int[] getSpringTemperatures() {
        return copyTemperatures(springTemperatures);
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the summer.
     */
    public int[] getSummerTemperatures() {
        return copyTemperatures(summerTemperatures);
    }

    /**
     * @return (double) The concentration of plants in the habitat.
     */
    public double getPlantConcentration() {
        return plantConcentration;
    }

    private int[] copyTemperatures(int[] seasonTemperatures)
    {
        return Arrays.copyOf(seasonTemperatures, seasonTemperatures.length);
    }
}
