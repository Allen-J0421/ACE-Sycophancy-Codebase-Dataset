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
    public Animal getAnimal(String animalType, Location location) {
        if(animalType == null) {
            return null;
        }
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        if (animalType.equalsIgnoreCase("SHEEP")) {
            return new Sheep(true, field, location, randomGender);
        }
        else if (animalType.equalsIgnoreCase("REINDEER")) {
            return new Reindeer(true, field, location, randomGender);
        }
        return null;
    }
}
