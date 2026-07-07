/**
 * Factory for creating herbivore animals. Registers SHEEP and REINDEER
 * suppliers; dispatches through the inherited map-based getAnimal().
 *
 * @version 2.0
 */
public class HerbivoreAnimalFactory extends AnimalFactory
{

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a HerbivoreAnimalFactory and registers all herbivore species.
     *
     * @param field Reference to field to later pass into animals.
     */
    public HerbivoreAnimalFactory(Field field)
    {
        super(field);
        register("SHEEP",    (loc, g) -> new Sheep(true, field, loc, g));
        register("REINDEER", (loc, g) -> new Reindeer(true, field, loc, g));
    }
}
