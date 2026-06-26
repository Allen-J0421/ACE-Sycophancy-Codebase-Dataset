import java.util.List;
/**
 * A minimalist implementation of a Bear, a bear can only eat other animals and not plants,
 * only the act method is unique to the bear.
 *
 * @version 1.0
 */
public class Bear extends CarnivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    private static final int BREEDING_AGE = 25;
    private static final int MAX_AGE = 80;
    private static final double BREEDING_PROBABILITY = 0.115;
    private static final double HIBERNATION_BREEDING_FACTOR = 0.5;
    private static final int BASE_HUNGER_LEVEL = 18;
    private static final int FEEDING_VALUE = 30;
    private static final List<Class<? extends Animal>> PREY_DIET = List.of(CarnivoreFox.class, Wolverine.class, Sheep.class, Reindeer.class);
    private static final int MAX_LITTER_SIZE = 3;
    
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
                randomAge,
                field,
                location,
                gender,
                BASE_HUNGER_LEVEL,
                MAX_AGE
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
    @Override
    public void act(List<Actor> newBears, Weather weather, DayState dayState)
    {
        // Bear will not act at night.
        if(dayState == DayState.NIGHT) {
            return;
        }

        double breedingProbability = BREEDING_PROBABILITY;
        if(weather == Weather.SNOW) {
            breedingProbability *= HIBERNATION_BREEDING_FACTOR;
        }
        actAsCarnivore(newBears, MAX_AGE, MAX_LITTER_SIZE, breedingProbability, BREEDING_AGE, PREY_DIET);
    }

    /**
     * Create a newborn bear.
     */
    @Override
    protected Animal createChild(Location location, Gender gender)
    {
        return new Bear(false, getField(), location, gender);
    }
    
    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten.
     * 
     * @return the feeding value.
     */
    @Override
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }
 }
