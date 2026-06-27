import java.util.Random;
import java.util.Map;
import java.util.Set;

/**
 * A simple model of a panther.
 * Pantheres age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Panther extends Animal
{
    // Characteristics shared by all pantheres (class variables).

    // The age at which a panther can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a panther can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a panther breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = Map.of(
        "Lemur", 10
    );
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the panther. In effect, this is the
    // number of steps a panther can go before it has to eat again.
    private static final int MAX_FOOD = 25;
    // String name for the panther
    public static final String name = "Panther";
    
    /**
     * Create a panther. The panther is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this panther had.
     */
    public Panther(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, name, MAX_AGE, MAX_FOOD, PREY_FOOD_VALUES);
        initializeNewbornState(true, true, false, parentDiseases);
    }

    public Panther(Time time, Field field, Location location)
    {
        super(time, field, location, name, MAX_AGE, MAX_FOOD, PREY_FOOD_VALUES);
        initializeRandomStartState(true, true, false);
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        return breedOffspringCount(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, rand);
    }

    /**
     * Creates a new panther 
     * If the panther is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new panther created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Panther(getTime(), getField(), loc, parentDiseases);
    }
}
