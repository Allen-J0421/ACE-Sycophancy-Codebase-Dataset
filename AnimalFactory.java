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
     * Creates an Animal given an input animal type.
     *
     * @param animalType the type of the animal
     * @param location The base location of the newly created animal
     * @return the created animal
     */
    public Animal getAnimal(String animalType, Location location)
    {
        return getAnimal(AnimalType.fromString(animalType), location);
    }

    /**
     * Creates an Animal given an input animal type.
     *
     * @param animalType the type of the animal
     * @param location The base location of the newly created animal
     * @return the created animal
     */
    public Animal getAnimal(AnimalType animalType, Location location)
    {
        if(animalType == null || !supports(animalType)) {
            return null;
        }
        return animalType.createWithRandomGender(field, location);
    }

    /**
     * Determine whether this factory can create the given animal type.
     *
     * @param animalType the requested animal type.
     * @return true when the type belongs to this factory's family.
     */
    protected abstract boolean supports(AnimalType animalType);
}
