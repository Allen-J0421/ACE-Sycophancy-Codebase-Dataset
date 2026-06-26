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
    
    /**
     * Creates and returns an animal given a particular type.
     * 
     * @param animalType The type of animal to create.
     * @param location The initial location of the newly created animal.
     * @return the newly created animal.
     */
    @Override
    public Animal getAnimal(AnimalType animalType, Location location) {
        if(animalType == null) {
            return null;
        }
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        switch(animalType) {
            case SHEEP:
                return new Sheep(true, field, location, randomGender);
            case REINDEER:
                return new Reindeer(true, field, location, randomGender);
            default:
                return null;
        }
    }
}
