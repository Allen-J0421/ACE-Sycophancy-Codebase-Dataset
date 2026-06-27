import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a monkey.
 * Monkeys age, move, eat dodos, and die.
 *
 * @version (28/02/2022)
 */
public class Monkey extends Animal
{
    // Characteristics shared by all monkeys (class variables).

    // The age at which a monkey can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a monkey can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a monkey breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The base rate which is used to give
    // the number of steps a predator gains when it eats a monkey
    private static final int BASIC_FOOD_LEVEL = 25;
    // Probability that a monkey dies from disease.
    private static final double MONKEY_DEATH_FROM_DISEASE_PROBABILITY = 0.05;
    // List of all monkey prey.
    private final ArrayList<Class> LIST_OF_PREY = new ArrayList<>() {
            {
                add(Dodo.class);
            }
        };

    /**
     * Create a monkey. A monkey can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the monkey will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     * @param infected  Boolean value determining if the animal is infected or not
     */
    protected Monkey(boolean randomAge, Field<Actor> field, Location location, boolean infected)
    {
        super(field, location, infected);

        // Sets the shared characteristics and starting state for a monkey.
        initialise(randomAge, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                   MAX_LITTER_SIZE, BASIC_FOOD_LEVEL, MONKEY_DEATH_FROM_DISEASE_PROBABILITY);
    }

    /**
     * Create a new born monkey, used by the breeding logic in Animal.
     *
     * @param field    The field the offspring is born into.
     * @param location The location the offspring is born at.
     * @return A new born monkey.
     */
    protected Animal createOffspring(Field<Actor> field, Location location)
    {
        return new Monkey(false, field, location, false);
    }

    /**
     * This is what the monkey does during the day: it hunts for dodos and tries to breed
     * In the process it might move, die of hunger, die of infection, get cured, spread an infection, or die of old age.
     *
     * @param newMonkeys A list to return newly born monkeys.
     */
    protected void dayAct(List<Actor> newMonkeys)
    {
        incrementAge();
        incrementHunger();
        dieInfection();

        if(isAlive()) {
            giveBirth(newMonkeys);
            cureInfected();
            spreadVirus();

            forageAndMove(LIST_OF_PREY);
        }
    }

    /**
     * This is what the monkey does during the night: Sleeps
     * In the process it might, die of infection
     * 
     * @param newMonkeys A list to return newly born monkeys.
     */
    protected void nightAct(List<Actor> newMonkeys)
    {
        dieInfection();
    }
}
