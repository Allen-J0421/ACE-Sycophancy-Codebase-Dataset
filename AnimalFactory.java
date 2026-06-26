/**
 * Factory for creating animals from explicit species definitions.
 *
 * @version 1.0
 */
public class AnimalFactory extends ActorFactory<Animal, AnimalSpecies>
{
    /**
     * Creates an AnimalFactory
     * @param reference to field to later pass in to animals
     */
    public AnimalFactory(Field field) 
    {
        super(field);
    }
}
