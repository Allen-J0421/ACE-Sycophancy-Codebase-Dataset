import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a human.
 * Humans age, move, eat dodos and pigs, and die.
 *
 * @version (28/02/2022)
 */
public class Human extends Animal
{
    // Characteristics shared by all human (class variables).

    // The age at which a human can start to breed.
    private static final int BREEDING_AGE = 18;
    // The age to which a human can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a human breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The base rate which is used to give
    // the number of steps a predator gains when it eats a human
    private static final int BASIC_FOOD_LEVEL = 25;
    // Probability that a human dies from disease.
    private static final double HUMAN_DEATH_FROM_DISEASE_PROBABILITY = 0.05;
    // List of all human prey.
    private final ArrayList<Class> LIST_OF_PREY = new ArrayList<>() {
            {
                add(Dodo.class);
                add(Pig.class);
            }
        };

    /**
     * Create a human. A human can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the human will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     * @param infected  Boolean value determining if the animal is infected or not.
     */
    protected Human(boolean randomAge, Field field, Location location, boolean infected)
    {
        super(field, location, infected);

        // Sets the shared characteristics and starting state for a human.
        initialise(randomAge, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                   MAX_LITTER_SIZE, BASIC_FOOD_LEVEL, HUMAN_DEATH_FROM_DISEASE_PROBABILITY);
    }

    /**
     * Create a new born human, used by the breeding logic in Animal.
     *
     * @param field    The field the offspring is born into.
     * @param location The location the offspring is born at.
     * @return A new born human.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Human(false, field, location, false);
    }

    /**
     * This is what the human does during the day: it hunts for dodos and pigs.
     * In the process it might move, die of hunger, die of infection, get cured, spread an infection, or die of old age.
     * 
     * @param newHuman A list to return newly born humans.
     */
    protected void dayAct(List<Actor> newHuman)
    {
        incrementAge();
        incrementHunger();
        dieInfection();

        if(isAlive()) {
            cureInfected();
            spreadVirus();

            forageAndMove(LIST_OF_PREY);
        }
    }

    /**
     * This is what the human does during the night: Gives birth and sleeps.
     * In the process it might, die of infection, spread an infection or get cured.
     * 
     * @param newHuman A list to return newly born humans.
     */
    protected void nightAct(List<Actor> newHuman)
    {
        dieInfection();

        if(isAlive()) {
            giveBirth(newHuman);
            cureInfected();
            spreadVirus();    
        }
    }
}
