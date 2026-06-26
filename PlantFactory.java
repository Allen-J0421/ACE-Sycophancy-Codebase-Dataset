/**
 * Factory for creating plants from explicit species definitions.
 *
 * @version 1.0
 */
public class PlantFactory extends ActorFactory<Plant, PlantSpecies>
{
    /**
     * Creates a PlantFactory.
     * 
     * @param field reference to field to later pass in to plants.
     */
    public PlantFactory(Field field)
    {
        super(field);
    }
}
