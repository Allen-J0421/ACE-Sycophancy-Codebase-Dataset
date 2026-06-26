/**
 * Shared behaviour between different animal factories
 *
 * @version 1.0
 */
public abstract class AnimalFactory
{
    
    protected Field field;
    
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
     * Creates an Animal given an input animal type
     * @param animalType the type of the animal
     * @param location The base location of the newly created animal
     * @return the created animal
     */
    abstract Animal getAnimal(AnimalType animalType, Location location);

    /**
     * Creates an Animal given an input animal type.
     * @param animalType the type of the animal
     * @param location The base location of the newly created animal
     * @return the created animal
     */
    Animal getAnimal(String animalType, Location location)
    {
        return getAnimal(AnimalType.fromString(animalType), location);
    }
}
