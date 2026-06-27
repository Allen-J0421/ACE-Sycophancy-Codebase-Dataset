import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * A simple model of a lion.
 * Lions age, move, find food (gazelles), find water, find mates, breed and die.
 *
 * @version 2022.02.27
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).
    
    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a lion can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births a lion can give.
    private static final int MAX_LITTER_SIZE = 4;
    // The probability of the animal successfully hunting their food
    private final double SUCCESSFUL_HUNT_PROB = 0.65;
    // // The food value of a lion - not eaten by any predators so initial
    // food value of newly generated lions
    private static final int LION_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom(); 
    // A list of the prey that the lion eats
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Gazelle"));

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(LION_FOOD_VALUE + 5);
        }
    }

    /**
     * This is what the lion does most of the time: it hunts for
     * gazelles. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newLions A list to return newly born lions.
     */
    @Override
    public void act(List<Actor> newLions)
    {
        super.act(newLions);
    }

    /**
     * Increase the age. This could result in the lion's death.
     */
    @Override
    public void incrementAge() {
        super.incrementAge();    
        if(this.getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLionss A list to return newly born lions.
     */
    @Override
    protected List<Location> giveBirth(List<Actor> newLions)
    {
        Field field = getField();
        int births = breed();
        List<Location> free = super.giveBirth(newLions);
        if (free != null) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Lion young = new Lion(false, field, loc);
                if (this.isInfected()) {
                    young.setInfected();
                }
                newLions.add(young);
            }
        }
        return null;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A lion can breed if it is female and has reached the breeding age.
     */
    private boolean canBreed()
    {
        return (this.isFemale() && this.getAge() >= BREEDING_AGE);
    }
    
    /**
     * @return The food value of a lion
     */
    @Override
    public int getFoodValue()
    { 
        return LION_FOOD_VALUE;
    }

    /**
     * @return The probability of a lion successfully hunting its prey.
     */
    @Override
    protected double getHuntProbability()
    {
        return SUCCESSFUL_HUNT_PROB;
    }
    
    /**
     * @return The list of prey which it eats 
     */
    @Override
    public List<String> getPrey()
    {
        return PREY;
    }
}
