import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a tortoise.
 * Tortoises age, move, eat plants, and die.
 *
 * @version (28/02/2022)
 */
public class Tortoise extends Animal
{
    // Characteristics shared by all tortoises (class variables).

    // The age at which a tortoise can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a tortoise can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a tortoise breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The base rate which is used to give
    // the number of steps a predator gains when it eats a tortoise
    private static final int BASIC_FOOD_LEVEL = 20;
    // Probability that a tortoise dies from disease.
    private static final double TORTOISE_DEATH_FROM_DISEASE_PROBABILITY = 0.015;
    // List of all tortoise prey.
    private final ArrayList<Class> LIST_OF_PREY = new ArrayList<>() {
            {
                add(Plant.class);
            }
        };

    /**
     * Create a tortoise. A tortoise can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the tortoise will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     * @param overlap   Whether or not an actor is allowed to overlap with other actors
     * @param infected  Boolean value determining if the animal is infected or not
     */
    protected Tortoise(boolean randomAge, Field field, Location location, boolean infected)
    {
        super(field, location, infected);

        // Sets the shared characteristics and starting state for a tortoise.
        initialise(randomAge, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                   MAX_LITTER_SIZE, BASIC_FOOD_LEVEL, TORTOISE_DEATH_FROM_DISEASE_PROBABILITY);
    }

    /**
     * Create a new born tortoise, used by the breeding logic in Animal.
     *
     * @param field    The field the offspring is born into.
     * @param location The location the offspring is born at.
     * @return A new born tortoise.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Tortoise(false, field, location, false);
    }

    /**
     * This is what the dodo does during the day: it looks for plants and tries to breed
     * In the process it might move, die of hunger, die of infection, get cured, spread an infection, or die of old age.
     * 
     * @param newTortoises A list to return newly born tortoises.
     */
    protected void dayAct(List<Actor> newTortoises)
    {
        incrementAge();
        incrementHunger();
        dieInfection();

        if(isAlive()) {
            giveBirth(newTortoises);
            cureInfected();
            spreadVirus();

            forageAndMove(LIST_OF_PREY);
        }
    }

    /**
     * This is what the tortoise does during the night: Sleeps
     * In the process it might, get cured or spread an infection
     * 
     * @param newTortoises A list to return newly born tortoises.
     */
    protected void nightAct(List<Actor> newTortoises)
    {
        if(alive){
            cureInfected();
            spreadVirus();
        } 
    }
}
