import java.util.*;

/**
 * A simple model of a coyote.
 * Coyotes age, move, eat prey,contract diseases and die.
 * @version 2022.03.3
 */
public class Coyote extends Animal
{
    // Characteristics shared by all coyotes (class variables).

    // The age at which a coyote can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a coyote can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a coyote breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single coyote
    private static final int FOOD_VALUE = 5;
    // The coyotes's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 15;
    // A set of organisms that a coyote consumes
    private static final Set<Class> DIET = new HashSet<>(Arrays.asList(Deer.class, Mouse.class));


    // Implementing abstract methods to return fields to be used by the superclass
    protected double BREEDING_AGE() { return BREEDING_AGE; }
    protected int MAX_LITTER_SIZE() { return MAX_LITTER_SIZE; }
    protected double BREEDING_PROBABILITY() { return BREEDING_PROBABILITY; }
    protected int MAX_AGE() { return MAX_AGE; }
    protected int MAX_FOOD_LEVEL() { return MAX_FOOD_LEVEL; }
    protected int FOOD_VALUE() { return FOOD_VALUE; }
    protected Set<Class> DIET() { return DIET; }

    /**
     * Create a coyote. A coyote can be created as a new-born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the coyote.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
        this.isNocturnal = true;
    }

    /**
     * Check whether this coyote is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCoyotes A list to return newly born coyotes.
     * @param environment The environment that the coyote resides in. 
     */
    protected void giveBirth(List<Actor> newCoyotes, Environment environment)
    {
        // New coyotes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);

            Gender sex = Randomizer.getRandomSex();

            Coyote young = new Coyote(false, field, loc, sex);
            newCoyotes.add(young);
        }
    }

}
