import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class reads from "habitats.csv" the information about all the habitats
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */
public class HabitatCSVReader
{
    // Name of the CSV file containing habitat data.
    private static final String FILE_NAME = "habitats.csv";
    // Centralised CSV file I/O.
    private final ConfigurationLoader loader;
    // Tool to alert user about any potential error.
    private final ErrorThrower errorThrower;

    // List of minimum, average, and maximum temperatures for each season.
    private int[] winterTemperatures;
    private int[] springTemperatures;
    private int[] summerTemperatures;
    private int[] autumnTemperatures;
    // The concentration of plants in a given habitat.
    private double plantConcentration;

    /**
     * Build a HabitatCSVReader and initialise its fields.
     */
    public HabitatCSVReader()
    {
        loader = new ConfigurationLoader();
        errorThrower = new ErrorThrower();
        resetParameters();
    }

    /**
     * Load and parse the row for the named habitat from the CSV file.
     *
     * @param habitatName (String) The habitat name to look up.
     */
    public void extractDataFor(String habitatName)
    {
        resetParameters();
        String[] data = loader.loadRow(FILE_NAME, habitatName);
        if (data != null) {
            populateFields(data);
        } else {
            System.out.println("ERROR: no data could be read for " + habitatName);
        }
    }

    /**
     * Return the list of habitat names available in the CSV file.
     *
     * @return (ArrayList<String>) All habitat names.
     */
    public ArrayList<String> getChoicesList()
    {
        return loader.loadNames(FILE_NAME);
    }

    /**
     * Parse a raw CSV row into the typed fields of this reader.
     * The habitat name (first column) is stripped before index-based parsing.
     *
     * @param extractedData (String[]) The raw CSV row.
     */
    private void populateFields(String[] extractedData)
    {
        extractedData = Arrays.copyOfRange(extractedData, 1, extractedData.length);
        if (extractedData.length != 9) {
            errorThrower.throwMessage("Habitat issue, please restart.");
        }

        for (int i = 0; i < extractedData.length; i++) {
            if (i / 2 == 0) {
                winterTemperatures[i % 2] = Integer.parseInt(extractedData[i]);
            } else if (i / 2 == 1) {
                springTemperatures[i % 2] = Integer.parseInt(extractedData[i]);
            } else if (i / 2 == 2) {
                summerTemperatures[i % 2] = Integer.parseInt(extractedData[i]);
            } else if (i / 2 == 3) {
                autumnTemperatures[i % 2] = Integer.parseInt(extractedData[i]);
            }
            plantConcentration = Double.valueOf(extractedData[8]);
        }
    }

    /**
     * Reset all fields to initial values before reading data for another habitat.
     */
    private void resetParameters()
    {
        winterTemperatures = new int[2];
        springTemperatures = new int[2];
        summerTemperatures = new int[2];
        autumnTemperatures = new int[2];
        plantConcentration = 0;
    }

    /** @return (int[]) Temperatures for winter. */
    public int[] getWinterTemperatures() { return winterTemperatures; }

    /** @return (int[]) Temperatures for autumn. */
    public int[] getAutumnTemperatures() { return autumnTemperatures; }

    /** @return (int[]) Temperatures for spring. */
    public int[] getSpringTemperatures() { return springTemperatures; }

    /** @return (int[]) Temperatures for summer. */
    public int[] getSummerTemperatures() { return summerTemperatures; }

    /** @return (double) The concentration of plants in the habitat. */
    public double getPlantConcentration() { return plantConcentration; }
}
