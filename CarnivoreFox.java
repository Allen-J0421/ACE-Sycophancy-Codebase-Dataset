import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Fox, a fox can only eat other animals and not plants,
 * only the act method is unique to the bear.
 *
 * @version 1.0
 */
public class CarnivoreFox extends CarnivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public static int FOUND_FOOD = 0;
    private static final int BREEDING_AGE = 12;
    private static final int MAX_AGE = 71;
    private static final double BREEDING_PROBABILITY = 0.076;
    private static final int BASE_HUNGER_LEVEL = 25;
    private static final int FEEDING_VALUE = 18;
    private static final Set<AnimalSpecies> PREY_DIET =
        EnumSet.of(AnimalSpecies.SHEEP, AnimalSpecies.REINDEER);
    private static final int MAX_LITTER_SIZE = 3;

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
     * Method in charge of the fox's action's during a step. During a step
     * a fox will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newFoxes the new sheeps to be born in case the sheep succesfully mates.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability(Weather weather)
    {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected Set<AnimalSpecies> getPreyDiet()
    {
        return PREY_DIET;
    }

    @Override
    protected boolean canAct(Weather weather, DayState dayState)
    {
        return dayState != DayState.NIGHT;
    }

    @Override
    protected void onFoodFound(Location location)
    {
        FOUND_FOOD++;
    }
    
    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten
     * @return the feeding value
     */
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }

    @Override
    protected AnimalSpecies getSpecies()
    {
        return AnimalSpecies.FOX;
    }
}
