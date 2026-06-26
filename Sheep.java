import java.util.List;
/**
 * A minimalist implementation of a Sheep, a sheep can only eat plants and not animals,
 * only the act method is unique to the sheep.
 *
 * @version 1.0
 */
public class Sheep extends HerbivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    private static final int BREEDING_AGE = 3;
    private static final int MAX_AGE = 20;
    private static final double BREEDING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int BASE_HUNGER_LEVEL = 6;
    private static final int FEEDING_VALUE = 18;
    private static final List<Class<? extends Plant>> TARGET_PLANTS = List.of(Grass.class, Sedge.class, Sage.class);
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a new Sheep.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public Sheep(boolean randomAge, Field field, Location location, Gender gender)
    {
        super(randomAge, field, location, gender, BASE_HUNGER_LEVEL, MAX_AGE);
    }
    
    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Method in charge of the sheep's action's during a step. During a step,
     * a sheep will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newSheeps the new sheeps to be born in case the sheep succesfully mates.
     */
    public void act(List<Actor> newSheeps, Weather weather, DayState dayState) 
    {
        actAsHerbivore(newSheeps, MAX_AGE, MAX_LITTER_SIZE, BREEDING_PROBABILITY, BREEDING_AGE, TARGET_PLANTS);
    }

    /**
     * Create a newborn sheep.
     */
    protected Animal createChild(Location location, Gender gender)
    {
        return new Sheep(false, getField(), location, gender);
    }
    
    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten
     * @return the feeding value
     */
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }
}
