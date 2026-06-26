/**
 * Factory Class responsible for creating animals that are herbivore
 *
 * @version 1.0
 */
public class HerbivoreAnimalFactory extends AnimalFactory
{
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates A CarnivoreAnimalFactory.
     * 
     * @param field reference to later pass into animals
     */
    public HerbivoreAnimalFactory(Field field) {
        super(field);
    }
    
    @Override
    protected boolean supports(AnimalType animalType)
    {
        return animalType != null && !animalType.isCarnivore();
    }
}
