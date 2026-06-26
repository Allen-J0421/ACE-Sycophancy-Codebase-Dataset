import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Bear, a bear can only eat other animals and not plants,
 * only the act method is unique to the bear.
 *
 * @version 1.0
 */
public class Bear extends SpeciesCarnivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    private static final Set<AnimalSpecies> PREY_DIET =
        EnumSet.of(
            AnimalSpecies.FOX,
            AnimalSpecies.WOLVERINE,
            AnimalSpecies.SHEEP,
            AnimalSpecies.REINDEER
        );
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new Bear.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public Bear(boolean randomAge, Field field, Location location, Gender gender)
    {
        super(
                AnimalSpecies.BEAR,
                randomAge,
                field,
                location,
                gender
                );
    }
    
    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Method in charge of the bear's action's during a step. During a step
     * a bear will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newBears the new bears to be born in case the sheep succesfully mates.
     */
    protected Set<AnimalSpecies> getPreyDiet()
    {
        return PREY_DIET;
    }
 }
