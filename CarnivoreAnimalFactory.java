/**
 * Factory Class responsible for creating animals that are carnivore.
 *
 * @version 1.1
 */
public class CarnivoreAnimalFactory extends AnimalFactory
{

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a Carnivore Animal Factory.
     *
     * @param field reference to later pass into animals
     */
    public CarnivoreAnimalFactory(Field field) {
        super(field);
    }

    /**
     * Registers the carnivore species this factory can create.
     */
    @Override
    protected void registerSpecies() {
        register("FOX", (location, gender) -> new CarnivoreFox(true, field, location, gender));
        register("WOLVERINE", (location, gender) -> new Wolverine(true, field, location, gender));
        register("BEAR", (location, gender) -> new Bear(true, field, location, gender));
    }
}
