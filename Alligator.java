import java.util.Random;
import java.util.Map;
import java.util.Set;

/**
 * A simple model of a alligator.
 * Alligators age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Alligator extends Animal
{
    // Characteristics shared by all alligators (class variables).

    // The age at which a alligator can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a alligator can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a alligator breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = Map.of(
        "Lemur", 8,
        "Catfish", 4
    );
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the alligator. In effect, this is the
    // number of steps a alligator can go before it has to eat again.
    private static final int MAX_FOOD = 25;
    // String name for the alligator
    public static final String name = "Alligator";

    /**
     * Create a alligator. The alligator is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this alligator had.
     */
    public Alligator(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        initializeNewbornState(false, true, true, MAX_FOOD, parentDiseases);
    }

    public Alligator(Time time, Field field, Location location)
    {
        super(time, field, location);
        initializeRandomStartState(false, true, true, MAX_AGE, MAX_FOOD, name);
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
     * Returns the String name.
     * @return The String name.
     */
    public String getActorName (){
        return name;
    }

    /**
     * Returns the max food level that the alligator can have.
     * @return The max food level that the alligator can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the alligator can have before dying.
     * @return The max age that the alligator can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the prey food values Map.
     * @return The prey food values Map.
     */
    public Map<String, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    /**
     * Creates a new alligator 
     * If the alligator is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new alligator created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Alligator(getTime(), getField(), loc, parentDiseases);
    }
}
