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
    
    /**
     * Creates and returns an animal given a particular type.
     * 
     * @param animalType The type of animal to create
     * @param location The initial location of the newly created animal
     * @return the newly created animal
     */
    public Animal getAnimal(String animalType, Location location) {
        if(animalType == null) {
            return null;
        }
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        if(animalType.equalsIgnoreCase("FOX")) {
            return new CarnivoreFox(true, field, location, randomGender);
        } 
        else if (animalType.equalsIgnoreCase("WOLVERINE")) {
            return new Wolverine(true, field, location, randomGender);
        }
        else if (animalType.equalsIgnoreCase("BEAR")) {
            return new Bear(true, field, location, randomGender);
        }
        return null;
    }
}
