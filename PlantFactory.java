import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
/**
 * Plant factory for creating objects that extend the Plant class.
 *
 * Plant types are registered against a constructor, so {@link #getPlant} is a
 * simple registry lookup rather than an if/else chain.
 *
 * @version 1.1
 */
public class PlantFactory
{

    /*///////////////////////////////////////////////////////////////
                                STATE
    //////////////////////////////////////////////////////////////*/

    private Field field;
    // Maps an (upper-cased) plant type name to a constructor producing that plant.
    private final Map<String, Function<Location, Plant>> registry = new HashMap<>();

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a Plant Factory.
     *
     * @param field reference to field to later pass in to plants.
     */
    public PlantFactory(Field field)
    {
        this.field = field;
        register("GRASS", location -> new Grass(true, field, location));
        register("SAGE", location -> new Sage(true, field, location));
        register("SEDGE", location -> new Sedge(true, field, location));
    }

    /**
     * Registers a constructor for a given plant type name.
     *
     * @param plantType the type name (matched case-insensitively).
     * @param constructor builds the plant from a location.
     */
    private void register(String plantType, Function<Location, Plant> constructor)
    {
        registry.put(plantType.toUpperCase(Locale.ROOT), constructor);
    }

    /**
     * Creates a Plant given an input plant type.
     *
     * @param plantType the type of the plant.
     * @param location The base location of the newly created plant.
     * @return the created plant, or null if the type is unknown.
     */
    public Plant getPlant(String plantType, Location location) {
        if(plantType == null) {
            return null;
        }
        Function<Location, Plant> constructor = registry.get(plantType.toUpperCase(Locale.ROOT));
        if(constructor == null) {
            return null;
        }
        return constructor.apply(location);
    }
}
