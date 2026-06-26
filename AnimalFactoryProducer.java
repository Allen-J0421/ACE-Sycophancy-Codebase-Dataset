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
    
    private final AnimalFactory carnivoreFactory;
    private final AnimalFactory herbivoreFactory;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates An animal factory producer.
     * 
     * @param field Reference to field to later pass in to animals.
     */
    
    public AnimalFactoryProducer(Field field) {
        carnivoreFactory = new CarnivoreAnimalFactory(field);
        herbivoreFactory = new HerbivoreAnimalFactory(field);
    }
    
    /**
     * Returns a Factory for either herbivore or carnivore animals.
     * 
     * @param isCarnivore Boolean flag to dictate whether to return herbivore or carnivore animal factory.
     */
    public AnimalFactory getFactory(boolean isCarnivore) {
        if(isCarnivore) {
            return carnivoreFactory;
        } else {
            return herbivoreFactory;
        }
    }
}
