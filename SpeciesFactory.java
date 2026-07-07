import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Responsible for creating Animal, Predator, and Plant instances by reading
 * their configuration from CSV data sources. Decouples species construction
 * from simulation initialization.
 *
 * @version 2022.03.01
 */
public class SpeciesFactory
{
    private final AnimalCSVReader animalReader;
    private final PlantCSVReader plantReader;

    /**
     * Create a SpeciesFactory backed by the given CSV readers.
     *
     * @param animalReader (AnimalCSVReader) Reader for animal configuration data.
     * @param plantReader (PlantCSVReader) Reader for plant configuration data.
     */
    public SpeciesFactory(AnimalCSVReader animalReader, PlantCSVReader plantReader)
    {
        this.animalReader = animalReader;
        this.plantReader = plantReader;
    }

    /**
     * Return the list of animal names available in the CSV source.
     *
     * @return (ArrayList<String>) The available animal choices.
     */
    public ArrayList<String> getAnimalChoices()
    {
        return animalReader.getChoicesList();
    }

    /**
     * Create a group of animals of the specified type. Reads configuration from
     * the CSV source, then instantiates the appropriate number of Animal or
     * Predator objects at locations provided by the supplier.
     *
     * @param animalName (String) The name of the animal type to create.
     * @param count (int) The number of animals to create.
     * @param field (Field) The simulation field.
     * @param locationSupplier (Supplier<Location>) Provides a free location for each animal.
     * @param randomAge (boolean) Whether each animal should be created with a random age.
     * @return (List<Species>) The created animals.
     */
    public List<Species> createAnimalGroup(String animalName, int count, Field field,
                                           Supplier<Location> locationSupplier, boolean randomAge)
    {
        List<Species> created = new ArrayList<>();
        animalReader.extractDataFor(animalName);

        String name = animalReader.getName();
        int maximumTemperature = animalReader.getMaximumTemperature();
        int minimumTemperature = animalReader.getMinimumTemperature();
        int maxAge = animalReader.getMaximumAge();
        int breedingAge = animalReader.getBreedingAge();
        double breedingProbability = animalReader.getBreedingProbability();
        int maxLitterSize = animalReader.getMaxLitterSize();
        int nutritionalValue = animalReader.getNutritionalValue();
        boolean hibernates = animalReader.canHibernate();
        boolean isNocturnal = animalReader.isNocturnal();

        if (animalReader.isPredator()) {
            int strength = animalReader.getStrength();
            for (int i = 0; i < count; i++) {
                created.add(new Predator(strength, field, locationSupplier.get(), name,
                        maximumTemperature, minimumTemperature, nutritionalValue,
                        breedingProbability, maxAge, breedingAge, maxLitterSize,
                        randomAge, hibernates, isNocturnal));
            }
        } else {
            for (int i = 0; i < count; i++) {
                created.add(new Animal(field, locationSupplier.get(), name,
                        maximumTemperature, minimumTemperature, nutritionalValue,
                        breedingProbability, maxAge, breedingAge, maxLitterSize,
                        randomAge, hibernates, isNocturnal));
            }
        }

        return created;
    }

    /**
     * Return the name of the animal type whose data was most recently loaded.
     * Must be called after createAnimalGroup.
     *
     * @return (String) The animal type name from the CSV data.
     */
    public String getLastAnimalName()
    {
        return animalReader.getName();
    }

    /**
     * Create a group of plants of the specified type. Reads configuration from
     * the CSV source, then instantiates the appropriate number of Plant objects
     * at locations provided by the supplier.
     *
     * @param plantName (String) The name of the plant type to create.
     * @param count (int) The number of plants to create.
     * @param field (Field) The simulation field.
     * @param locationSupplier (Supplier<Location>) Provides a free location for each plant.
     * @return (List<Species>) The created plants.
     */
    public List<Species> createPlantGroup(String plantName, int count, Field field,
                                          Supplier<Location> locationSupplier)
    {
        List<Species> created = new ArrayList<>();
        plantReader.extractDataFor(plantName);

        String name = plantReader.getName();
        int maximumTemperature = plantReader.getMaximumTemperature();
        int minimumTemperature = plantReader.getMinimumTemperature();
        int nutritionalValue = plantReader.getNutritionalValue();
        double reproductionProbability = plantReader.getReproductionProbability();
        int maxHealth = plantReader.getMaxHealth();

        for (int i = 0; i < count; i++) {
            created.add(new Plant(field, locationSupplier.get(), name,
                    maximumTemperature, minimumTemperature, nutritionalValue,
                    reproductionProbability, maxHealth));
        }

        return created;
    }

    /**
     * Return the name of the plant type whose data was most recently loaded.
     * Must be called after createPlantGroup.
     *
     * @return (String) The plant type name from the CSV data.
     */
    public String getLastPlantName()
    {
        return plantReader.getName();
    }
}
