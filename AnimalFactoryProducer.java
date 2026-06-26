/**
 * Producer class for creating Animal factories.
 *
 * @version 1.0
 */
public class AnimalFactoryProducer
{
    
    /*///////////////////////////////////////////////////////////////
                                STATE
    //////////////////////////////////////////////////////////////*/   
    
    private Field field;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates An animal factory producer.
     * 
     * @param field Reference to field to later pass in to animals.
     */
    
    public AnimalFactoryProducer(Field field) {
        this.field = field;
    }
    
    /**
     * Returns a Factory for the family that owns the given species.
     *
     * @param animalType species to create.
     * @return a factory for the species family.
     */
    public AnimalFactory getFactory(AnimalType animalType)
    {
        if(animalType == null) {
            return null;
        }
        return animalType.isCarnivore() ? new CarnivoreAnimalFactory(field) : new HerbivoreAnimalFactory(field);
    }
    
    /**
     * Returns a Factory for either herbivore or carnivore animals.
     * 
     * @param isCarnivore Boolean flag to dictate whether to return herbivore or carnivore animal factory.
     */
    public AnimalFactory getFactory(boolean isCarnivore) {
        return isCarnivore ? new CarnivoreAnimalFactory(field) : new HerbivoreAnimalFactory(field);
    }
}
