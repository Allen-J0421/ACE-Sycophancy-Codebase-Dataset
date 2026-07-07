import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Centralizes CSV file I/O for all configuration sources in the simulation.
 * Consumed by composition in AnimalCSVReader, PlantCSVReader, HabitatCSVReader,
 * and ClimateScenarioFactory, replacing the former CSVReader inheritance hierarchy.
 *
 * @version 2022.03.01
 */
public class ConfigurationLoader
{
    /**
     * Read the CSV file and return the first row whose first column equals the
     * given key. Returns null if the key is not found or the file cannot be read.
     *
     * @param fileName (String) Path to the CSV file.
     * @param key (String) The value to match in the first column.
     * @return (String[]) The matched row's fields, or null.
     */
    public String[] loadRow(String fileName, String key)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header row
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                if (attributes[0].equals(key)) {
                    return attributes;
                }
            }
        } catch (Exception e) {
            System.out.println("Issue when parsing CSV: " + fileName);
        }
        return null;
    }

    /**
     * Read the CSV file and return a list of every first-column value (i.e. the
     * name of each entry). Returns null if the file cannot be read.
     *
     * @param fileName (String) Path to the CSV file.
     * @return (ArrayList<String>) All first-column values, or null.
     */
    public ArrayList<String> loadNames(String fileName)
    {
        ArrayList<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header row
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                names.add(attributes[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Issue when parsing CSV: " + fileName);
            return null;
        }
        return names;
    }
}
