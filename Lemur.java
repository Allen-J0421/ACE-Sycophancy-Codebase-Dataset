import java.util.Random;
import java.util.Map;
import java.util.Set;

/**
 * A simple model of a lemur.
 * Lemurs age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Lemur extends Animal
{
    // Characteristics shared by all lemurs (class variables).

    // The age at which a lemur can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a lemur can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a lemur breeding.
    private static final double BREEDING_PROBABILITY = 0.7;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE =5;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = Map.of(
        "Grass", 3,
        "Salamander", 4
    );
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the lemur. In effect, this is the
    // number of steps a lemur can go before it has to eat again.
    private static final int MAX_FOOD = 9;
    // String name for the lemur
    public static final String name = "Lemur";

    /**
     * Create a lemur. The lemur is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this lemur had.
     */
    public Lemur(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, name, MAX_AGE, MAX_FOOD, PREY_FOOD_VALUES);
        initializeNewbornState(false, true, false, parentDiseases);
    }

    public Lemur(Time time, Field field, Location location)
    {
        super(time, field, location, name, MAX_AGE, MAX_FOOD, PREY_FOOD_VALUES);
        initializeRandomStartState(false, true, false);
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
     * Creates a new lemur 
     * If the lemur is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new lemur created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Lemur(getTime(), getField(), loc, parentDiseases);
    }
}
