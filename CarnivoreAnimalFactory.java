/**
 * Factory for creating carnivore animals. Registers FOX, WOLVERINE, and BEAR
 * suppliers; dispatches through the inherited map-based getAnimal().
 *
 * @version 2.0
 */
public class CarnivoreAnimalFactory extends AnimalFactory
{

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a CarnivoreAnimalFactory and registers all carnivore species.
     *
     * @param field Reference to field to later pass into animals.
     */
    public CarnivoreAnimalFactory(Field field)
    {
        super(field);
        register("FOX",      (loc, g) -> new CarnivoreFox(true, field, loc, g));
        register("WOLVERINE",(loc, g) -> new Wolverine(true, field, loc, g));
        register("BEAR",     (loc, g) -> new Bear(true, field, loc, g));
    }
}
