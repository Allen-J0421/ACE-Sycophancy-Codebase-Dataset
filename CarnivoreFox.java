import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Fox, a fox can only eat other animals and not plants,
 * only the act method is unique to the bear.
 *
 * @version 1.0
 */
public class CarnivoreFox extends SpeciesCarnivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public static int FOUND_FOOD = 0;
    private static final Set<AnimalSpecies> PREY_DIET =
        EnumSet.of(AnimalSpecies.SHEEP, AnimalSpecies.REINDEER);

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a new Fox.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public CarnivoreFox(boolean randomAge, Field field, Location location, Gender gender)
    {
        super(
                AnimalSpecies.FOX,
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
     * Method in charge of the fox's action's during a step. During a step
     * a fox will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newFoxes the new sheeps to be born in case the sheep succesfully mates.
     */
    protected Set<AnimalSpecies> getPreyDiet()
    {
        return PREY_DIET;
    }

    @Override
    protected void onFoodFound(Location location)
    {
        FOUND_FOOD++;
    }
}
