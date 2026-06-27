import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a pig.
 * Pigs age, move, eat dodos, and die.
 *
 * @version (28/02/2022)
 */
public class Pig extends Animal
{
    // Characteristics shared by all pigs (class variables).

    // The age at which a pig can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a pig can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a pig breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The base rate which is used to give
    // the number of steps a predator gains when it eats a pig
    private static final int BASIC_FOOD_LEVEL = 18;
    // Probability that a pig dies from disease.
    private static final double PIG_DEATH_FROM_DISEASE_PROBABILITY = 0.025;
    // List of all pig prey.
    private final ArrayList<Class> LIST_OF_PREY = new ArrayList<>() {
            {
                add(Dodo.class);
            }
        };

    /**
     * Create a pig. A pig can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the pig will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     * @param infected  Boolean value determining if the animal is infected or not
     */
    protected Pig(boolean randomAge, Field<Actor> field, Location location, boolean infected)
    {
        super(field, location, infected);

        // Sets the shared characteristics and starting state for a pig.
        initialise(randomAge, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                   MAX_LITTER_SIZE, BASIC_FOOD_LEVEL, PIG_DEATH_FROM_DISEASE_PROBABILITY);
    }

    /**
     * Create a new born pig, used by the breeding logic in Animal.
     *
     * @param field    The field the offspring is born into.
     * @param location The location the offspring is born at.
     * @return A new born pig.
     */
    protected Animal createOffspring(Field<Actor> field, Location location)
    {
        return new Pig(false, field, location, false);
    }

    /**
     * This is what the pig does during the day: it hunts for dodos and tries to breed
     * In the process it might move, die of hunger, die of infection, get cured, spread an infection, or die of old age.
     * 
     * @param newPigs A list to return newly born pigs.
     */
    protected void dayAct(List<Actor> newPigs)
    {
        incrementAge();
        incrementHunger();
        dieInfection();

        if(isAlive()) {
            giveBirth(newPigs);
            cureInfected();
            spreadVirus();

            forageAndMove(LIST_OF_PREY);
        }
    }

    /**
     * This is what the pig does during the night: Sleeps
     * In the process it might, die of infection
     * 
     * @param newPigs A list to return newly born pigs.
     */
    protected void nightAct(List<Actor> newPigs)
    {
        dieInfection();
    }
}
