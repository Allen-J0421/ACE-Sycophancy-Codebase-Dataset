import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to govern behavior of the various CSV Readers (one for plants, animals, scenarios, and habitats).
 * This class implements the actual "reading from a .csv file behavior" behaviour.
 *
 * @version 2022.02.28
 */
public abstract class CSVReader
{
    private static final String CSV_SEPARATOR = ",";

    /**
     * Empty constructor for this class.
     */
    public CSVReader()
    {
    }

    /**
     * Method to be called by children, it centralizes the CSV Readers operations by
     * orchestrating the process of reading data from the .csv file and populating
     * the appropriate fields with the read data.
     *
     * @param nameOfElementToLookFor (String) the name of the element which data must be extracted
     */
    public void extractDataFor(String nameOfElementToLookFor)
    {
        resetParameters();
        String[] extractedData = findRowByName(nameOfElementToLookFor);
        if (extractedData != null) {
            populateFields(extractedData);
        } else {
            System.out.println("ERROR: no data could be read for " + nameOfElementToLookFor);
        }
    }

    /**
     * Reads the data relative to a given element in the appropriate .csv file
     * (the path to this file depends on the children class from which this method is called)
     * Source: technique to read .csv files was found on https://stackabuse.com/reading-and-writing-csvs-in-java/
     *
     * @param nameOfElementToLookFor (String) the name of the element which data must be extracted
     * @return (String[]) the extracted data
     */
    private String[] findRowByName(String nameOfElementToLookFor)
    {
        for (String[] row : readDataRows()) {
            if (row[0].equals(nameOfElementToLookFor)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Abstract method that must be overridden with the appropriate behaviours
     * needed to populate the fields of the child class.
     *
     * @param extractedData (String[]) The data extracted from the .csv file.
     */
    abstract void populateFields(String[] extractedData);

    /**
     * Abstract method used to reset all the fields of a CSV Reader object when
     * the reading is done and another element ca be expected to be read.
     */
    abstract void resetParameters();

    /**
     * Abstract method to return the name of the file containing the data to be read.
     *
     * @return (String) the file name.
     */
    abstract String getFileName();

    /**
     * Returns a list of choices (of animals, habitats, and scenarios depending
     * on the child class it is called from) available to the user.
     * Source: technique to read from .csv files was found on https://stackabuse.com/reading-and-writing-csvs-in-java/
     *
     * @return (List<String>) The list of available choices.
     */
    public List<String> getChoicesList()
    {
        List<String> choicesList = new ArrayList<>();
        for (String[] row : readDataRows()) {
            choicesList.add(row[0]);
        }
        return choicesList;
    }

    private List<String[]> readDataRows()
    {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getFileName()))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                rows.add(line.split(CSV_SEPARATOR));
            }
        } catch (Exception e) {
            System.out.println("Issue when parsing CSV");
        }
        return rows;
    }
}
