import java.util.Random;
import java.util.List;
import java.util.*;

/**
 * A simple model of a eagle.
 * Eagles age, move,contract diseases, eat prey, and die.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
    private static final TargetAcquisitionPolicy TARGET_POLICY = new EagleTargetAcquisitionPolicy();
     // Characteristics shared by all eagles (class variables).

    // The age at which a eagle can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a eagle can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The eagles's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 14;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single eagle.
    private static final int FOOD_VALUE = 5;
    // A set of organisms that a eagle consumes
    private static final Set<Class<?>> DIET = Set.of(Deer.class, Coyote.class, Mouse.class);

    // Implementing abstract methods to return fields to be used by the superclass
    protected double BREEDING_AGE() { return BREEDING_AGE; }
    protected int MAX_LITTER_SIZE() { return MAX_LITTER_SIZE; }
    protected double BREEDING_PROBABILITY() { return BREEDING_PROBABILITY; }
    protected int MAX_AGE() { return MAX_AGE; }
    protected int MAX_FOOD_LEVEL() { return MAX_FOOD_LEVEL; }
    public int getFoodValue() { return FOOD_VALUE; }
    protected Set<Class<?>> DIET() { return DIET; }

    @Override
    protected Location locateTargetLocation(Environment environment)
    {
        return TARGET_POLICY.acquireTarget(this, environment);
    }

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location,randomAge,sex);
        this.isNocturnal = false;
    }

    
    /**
     * Check whether this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newEagles A list to return newly born Eagles.
     * @param environment The environment that the eagle resides in. 
     */
    protected void giveBirth(List<Actor> newEagles, Environment environment)
    {
        // New Eagles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Gender sex = Randomizer.getRandomSex();
            Eagle young = new Eagle(false, field, loc, sex);
            newEagles.add(young);
        }
    }

    @Override
    protected void onMovementBlocked()
    {
        // Eagles stay alive if no movement is possible.
    }
}
