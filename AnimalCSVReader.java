/**
 * This class reads from "animals.csv" the information about all the animals
 * that are available to be put in the simulation.
 *
 * @version 2022.02.28
 */

public class AnimalCSVReader extends CSVReader
{
    // Path to the file holding the animal related data.
    private static final String FILE_NAME = "animals.csv";
    // String to be recognized as a boolean value of true.
    private static final String TRUE_SYMBOL = "true";
    private static final int EXPECTED_COLUMN_COUNT = 12;
    private static final int NAME_COLUMN = 0;
    private static final int PREDATOR_COLUMN = 1;
    private static final int MAXIMUM_TEMPERATURE_COLUMN = 2;
    private static final int MINIMUM_TEMPERATURE_COLUMN = 3;
    private static final int MAXIMUM_AGE_COLUMN = 4;
    private static final int BREEDING_AGE_COLUMN = 5;
    private static final int BREEDING_PROBABILITY_COLUMN = 6;
    private static final int MAX_LITTER_SIZE_COLUMN = 7;
    private static final int NUTRITIONAL_VALUE_COLUMN = 8;
    private static final int STRENGTH_COLUMN = 9;
    private static final int HIBERNATES_COLUMN = 10;
    private static final int NOCTURNAL_COLUMN = 11;
    // Parsed animal profile from the latest CSV extraction.
    private AnimalProfile animalProfile;
    // Tool to alert user about any potential error.
    private ErrorThrower errorThrower;

    /**
     * Builds an AnimalCSVReader and initializes field.
     */
    public AnimalCSVReader() {
        errorThrower = new ErrorThrower();
        resetParameters();
    }

    /**
     * Populated fields with the data read from the files. This method overrides
     * a method of the CSVReader parent class and is therefore called after reading the data.
     *
     * @param extractedData (String[]) The data read.
     */
    protected void populateFields(String[] extractedData)
    {
        if (extractedData.length != EXPECTED_COLUMN_COUNT) {
            errorThrower.throwMessage("Animal .csv issue, please restart.");
        }
        animalProfile = new AnimalProfile(
            extractedData[NAME_COLUMN],
            extractedData[PREDATOR_COLUMN].equals(TRUE_SYMBOL),
            Integer.valueOf(extractedData[MAXIMUM_TEMPERATURE_COLUMN]),
            Integer.valueOf(extractedData[MINIMUM_TEMPERATURE_COLUMN]),
            Integer.valueOf(extractedData[MAXIMUM_AGE_COLUMN]),
            Integer.valueOf(extractedData[BREEDING_AGE_COLUMN]),
            Double.valueOf(extractedData[BREEDING_PROBABILITY_COLUMN]),
            Integer.valueOf(extractedData[MAX_LITTER_SIZE_COLUMN]),
            Integer.valueOf(extractedData[NUTRITIONAL_VALUE_COLUMN]),
            Integer.valueOf(extractedData[STRENGTH_COLUMN]),
            extractedData[HIBERNATES_COLUMN].equals(TRUE_SYMBOL),
            extractedData[NOCTURNAL_COLUMN].equals(TRUE_SYMBOL)
        );
    }

    /**
     * Set all parameters back to their initial values before reading data for another animal.
     */
    protected void resetParameters()
    {
        animalProfile = new AnimalProfile(null, false, 0, 0, 0, 0, 0, 0, 0, 0, false, false);
    }

    /**
     * @return (String) The name of the file containing habitat data.
     */
    protected String getFileName() {
        return FILE_NAME;
    }

    /**
     * @return (String) The animal's name.
     */
    public String getName() {
        return animalProfile.getName();
    }

    /**
     * @return (double) Probability that the animal breeds at a given step.
     */
    public double getBreedingProbability() {
        return animalProfile.getReproductionProbability();
    }

    /**
     * @return (int) Maximum age animal can live for.
     */
    public int getMaximumAge() {
        return animalProfile.getMaximumAge();
    }

    /**
     * @return (int) Maximum temperature animal can survive to.
     */
    public int getMaximumTemperature() {
        return animalProfile.getMaximumTemperature();
    }

    /**
     * @return (int) Minimum age for animal to start breeding.
     */
    public int getBreedingAge() {
        return animalProfile.getBreedingAge();
    }

    /**
     * @return (int) Minimum temperature animal can survive to.
     */
    public int getMinimumTemperature() {
        return animalProfile.getMinimumTemperature();
    }

    /**
     * @return (int) Maximum litter size brought by animal in one reproduction.
     */
    public int getMaxLitterSize() {
        return animalProfile.getMaxLitterSize();
    }

    /**
     * @return (int) Nutritional value brought when animal is eaten.
     */
    public int getNutritionalValue() {
        return animalProfile.getNutritionalValue();
    }

    /**
     * @return (int) Animal's strength (0 if not a predator).
     */
    public int getStrength() {
        return animalProfile.getStrength();
    }

    /**
     * @return (boolean) If animal is a predator.
     */
    public boolean isPredator() {
        return animalProfile.isPredator();
    }

    /**
     * @return (boolean) Whether or not animal can hibernate.
     */
    public boolean canHibernate() {
        return animalProfile.canHibernate();
    }

    /**
     * @return (boolean) Whether or not animal is nocturnal.
     */
    public boolean isNocturnal()
    {
        return animalProfile.isNocturnal();
    }

    /**
     * @return (AnimalProfile) The immutable profile represented by the last extracted CSV row.
     */
    public AnimalProfile getAnimalProfile()
    {
        return animalProfile;
    }
}
