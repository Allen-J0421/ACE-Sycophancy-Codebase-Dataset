/**
 * Factory Class responsible for creating animals that are herbivore
 *
 * @version 1.1
 */
public class HerbivoreAnimalFactory extends AnimalFactory
{

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a HerbivoreAnimalFactory.
     *
     * @param field reference to later pass into animals
     */
    public HerbivoreAnimalFactory(Field field) {
        super(field);
    }

    /**
     * Registers the herbivore species this factory can create.
     */
    @Override
    protected void registerSpecies() {
        register("SHEEP", (location, gender) -> new Sheep(true, field, location, gender));
        register("REINDEER", (location, gender) -> new Reindeer(true, field, location, gender));
    }
}
