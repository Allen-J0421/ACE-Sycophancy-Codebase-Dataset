/**
 * Factory Class responsible for creating animals that are carnivore.
 *
 * @version 1.0
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
    
    @Override
    protected boolean supports(AnimalType animalType)
    {
        return animalType != null && animalType.isCarnivore();
    }
}
