import java.util.Arrays;

/**
 * This class reads from "habitats.csv" the information about all the habitats
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */

public class HabitatCSVReader extends CSVReader {
    // List of minimum, average, and maximum temperatures for the winter.
    private int[] winterTemperatures;
    // List of minimum, average, and maximum temperatures for the spring.
    private int[] springTemperatures;
    // List of minimum, average, and maximum temperatures for the summer.
    private int[] summerTemperatures;
    // List of minimum, average, and maximum temperatures for the autumn.
    private int[] autumnTemperatures;
    // The concentration of plants in a given habitat.
    private double plantConcentration;
    // Tool to alert user about any potential error.
    private ErrorThrower errorThrower;

    // Name of the CSV files containing data on fields.
    private static final String FILE_NAME = "habitats.csv";

    // Column indices within the data row after the habitat name is removed.
    // Each season occupies two consecutive indices: [avgTemp, tempChange].
    private static final int WINTER_AVG_TEMP_IDX    = 0;
    private static final int WINTER_TEMP_CHANGE_IDX = 1;
    private static final int SPRING_AVG_TEMP_IDX    = 2;
    private static final int SPRING_TEMP_CHANGE_IDX = 3;
    private static final int SUMMER_AVG_TEMP_IDX    = 4;
    private static final int SUMMER_TEMP_CHANGE_IDX = 5;
    private static final int AUTUMN_AVG_TEMP_IDX    = 6;
    private static final int AUTUMN_TEMP_CHANGE_IDX = 7;
    private static final int PLANT_CONCENTRATION_IDX = 8;
    private static final int EXPECTED_FIELD_COUNT    = 9;

    /**
     * Build a HabitatCSVReader and initialize its fields.
     */
    public HabitatCSVReader() {
        errorThrower = new ErrorThrower();
        winterTemperatures = new int[2];
        autumnTemperatures = new int[2];
        springTemperatures = new int[2];
        summerTemperatures = new int[2];
        plantConcentration = 0;
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
        if (extractedData.length != EXPECTED_FIELD_COUNT) {
            errorThrower.throwMessage("Habitat issue, please restart.");
        }

        winterTemperatures[0] = Integer.parseInt(extractedData[WINTER_AVG_TEMP_IDX]);
        winterTemperatures[1] = Integer.parseInt(extractedData[WINTER_TEMP_CHANGE_IDX]);
        springTemperatures[0] = Integer.parseInt(extractedData[SPRING_AVG_TEMP_IDX]);
        springTemperatures[1] = Integer.parseInt(extractedData[SPRING_TEMP_CHANGE_IDX]);
        summerTemperatures[0] = Integer.parseInt(extractedData[SUMMER_AVG_TEMP_IDX]);
        summerTemperatures[1] = Integer.parseInt(extractedData[SUMMER_TEMP_CHANGE_IDX]);
        autumnTemperatures[0] = Integer.parseInt(extractedData[AUTUMN_AVG_TEMP_IDX]);
        autumnTemperatures[1] = Integer.parseInt(extractedData[AUTUMN_TEMP_CHANGE_IDX]);
        plantConcentration    = Double.parseDouble(extractedData[PLANT_CONCENTRATION_IDX]);
    }

    /**
     * Set all parameters back to initial values before reading data for another habitat.
     */
    protected void resetParameters()
    {
        winterTemperatures = new int[2];
        springTemperatures = new int[2];
        summerTemperatures = new int[2];
        autumnTemperatures = new int[2];
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
        return winterTemperatures;
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the autumn.
     */
    public int[] getAutumnTemperatures() {
        return autumnTemperatures;
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the spring.
     */
    public int[] getSpringTemperatures() {
        return springTemperatures;
    }

    /**
     * @return (int[]) Minimum, average, and maximum temperatures for the summer.
     */
    public int[] getSummerTemperatures() {
        return summerTemperatures;
    }

    /**
     * @return (double) The concentration of plants in the habitat.
     */
    public double getPlantConcentration() {
        return plantConcentration;
    }
}