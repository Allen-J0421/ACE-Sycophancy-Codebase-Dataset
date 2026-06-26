/**
 * Factory for creating animals from explicit species definitions.
 *
 * @version 1.0
 */
public class AnimalFactory
{
    
    private final Field field;
    
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
    }
    
    /**
     * Creates an animal of the given species.
     *
     * @param species the species to create.
     * @param location The base location of the newly created animal
     * @return the created animal
     */
    public Animal create(AnimalSpecies species, Location location)
    {
        if(species == null) {
            return null;
        }
        return species.createRandom(field, location);
    }
}
