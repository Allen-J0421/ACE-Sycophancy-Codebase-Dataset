import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
/**
 * Concrete factory for creating Animals. Subclasses register species-specific
 * suppliers via register(); getAnimal() dispatches through the registry.
 *
 * @version 2.0
 */
public class AnimalFactory
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    protected Field field;
    private final Map<String, BiFunction<Location, Gender, Animal>> registry = new HashMap<>();

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates an AnimalFactory.
     *
     * @param field Reference to field to later pass into animals.
     */
    public AnimalFactory(Field field)
    {
        this.field = field;
    }

    /*///////////////////////////////////////////////////////////////
                             REGISTRY MANAGEMENT
    //////////////////////////////////////////////////////////////*/

    /**
     * Registers a supplier for a given animal type key.
     *
     * @param type     Case-insensitive species key (e.g. "FOX").
     * @param supplier BiFunction that takes (Location, Gender) and returns a new Animal.
     */
    protected void register(String type, BiFunction<Location, Gender, Animal> supplier)
    {
        registry.put(type.toUpperCase(), supplier);
    }

    /*///////////////////////////////////////////////////////////////
                              FACTORY METHOD
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates and returns an animal of the given type at the given location.
     * Consumes one RNG value for gender regardless of whether the type is matched,
     * preserving the RNG sequence of the original if-else dispatch.
     *
     * @param animalType The type of animal to create.
     * @param location   The initial location of the newly created animal.
     * @return The newly created animal, or null if type is null or unregistered.
     */
    public Animal getAnimal(String animalType, Location location)
    {
        if (animalType == null) {
            return null;
        }
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        BiFunction<Location, Gender, Animal> supplier = registry.get(animalType.toUpperCase());
        if (supplier == null) {
            return null;
        }
        return supplier.apply(location, randomGender);
    }
}
