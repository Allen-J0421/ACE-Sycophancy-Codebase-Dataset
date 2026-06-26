import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a dingo.
 * Dingoes age, move, eat snakes, and die.
 *
 * @version 01.03.22
 */
public class Dingo extends Animal
{
    // Characteristics shared by all dingoes (class variables).
    
    // The age at which a dingo can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a dingo can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a dingo breeding.
    private static final double BREEDING_PROBABILITY = 0.04;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single snake. In effect, this is the
    // number of steps a dingo can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a dingo. A dingo can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dingo(boolean randomAge, Field field, Location location) {
        super(field, location);
        initializeAnimal(randomAge, rand, MAX_AGE, SNAKE_FOOD_VALUE);
    }
    
    /**
     * This is what the dingo does most of the time: it hunts for
     * snakes. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newDingoes A list to return newly born dingoes.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newDingoes, int time) {
        performAct(newDingoes, time, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
    }

    protected boolean isActiveAt(int time) {
        return time >= 8 && time <= 24;
    }

    protected Animal createYoung(Field field, Location location) {
        return new Dingo(false, field, location);
    }

    /**
     * Look for snakes adjacent to the current location.
     * Only the first live snake is eaten.
     * If it is a plant, then it is 'trampled'
     * @return where food was found, or null if it wasn't.
     */
    protected Location findFood() {
        if (!canSearchInFog(rand)) {
            return null;
        }

        List<Location> adjacent = adjacentLocations();
        Location food = eatAdjacentAnimal(adjacent, Snake.class, SNAKE_FOOD_VALUE);
        if(food == null) {
            food = trampleAdjacentPlant(adjacent);
        }
        return food;
    }
}
