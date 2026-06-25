import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.02.24 (2)
 */
public abstract class Animal extends LivingBeing
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    protected SEX gender;
    //enum for M or F 
    private enum SEX { MALE, FEMALE} 
    /**
     * Create a new animal at location in field.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field,location);
        gender = randomGender(); // Assigns a random gender to the animal 
    }

    /**
     * Assign a random gender to the animal
     * Called in Animal Constructor
     * @return MALE or FEMALE
     */
    private SEX randomGender() {
        int g = new Random().nextInt(SEX.values().length);
        return SEX.values()[g];
    }

    /**
     * Returns true if animal is Female, false if Male
     * @param first animal to compare sex
     * @param second animal to compare sex
     */
    protected boolean getOppositeGender(Animal animal1, Animal animal2) {
        if(animal1.gender != animal2.gender) {
            return true; //The animals are of opposite genders
        }
        return false; //The animals are of the same gender
    }
    
    /**
     * Abstract method act, defined in subclasses (meant to be overridden)
     * @param newAnimals a list of new animals 
     */
    abstract public void act(List<LivingBeing> newAnimals);
    
}
