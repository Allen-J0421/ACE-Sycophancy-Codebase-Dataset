import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
/**
 * Shared behaviour between different animal factories.
 *
 * Concrete factories declare the species they can build by registering a
 * constructor against a type name in {@link #registerSpecies()}; the shared
 * {@link #getAnimal(String, Location)} dispatch then looks the constructor up,
 * removing the need for per-factory if/else chains.
 *
 * @version 1.1
 */
public abstract class AnimalFactory
{

    protected Field field;
    // Maps an (upper-cased) animal type name to a constructor producing that animal.
    private final Map<String, BiFunction<Location, Gender, Animal>> registry = new HashMap<>();

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates an AnimalFactory
     * @param reference to field to later pass in to animals
     */
    public AnimalFactory(Field field)
    {
        this.field = field;
        registerSpecies();
    }

    /**
     * Registers the species this factory can create by calling
     * {@link #register(String, BiFunction)} for each one.
     */
    protected abstract void registerSpecies();

    /**
     * Registers a constructor for a given animal type name.
     *
     * @param animalType the type name (matched case-insensitively).
     * @param constructor builds the animal from a location and gender.
     */
    protected void register(String animalType, BiFunction<Location, Gender, Animal> constructor)
    {
        registry.put(animalType.toUpperCase(Locale.ROOT), constructor);
    }

    /**
     * Creates an Animal given an input animal type.
     *
     * @param animalType the type of the animal
     * @param location The base location of the newly created animal
     * @return the created animal, or null if the type is unknown.
     */
    public Animal getAnimal(String animalType, Location location)
    {
        if(animalType == null) {
            return null;
        }
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        BiFunction<Location, Gender, Animal> constructor = registry.get(animalType.toUpperCase(Locale.ROOT));
        if(constructor == null) {
            return null;
        }
        return constructor.apply(location, randomGender);
    }
}
