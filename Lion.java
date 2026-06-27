import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

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
    // A list of the prey that the lion eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Gazelle"));

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
    public void act(List<Actor> newLions)
    {
        super.act(newLions);
    }

    /**
     * Look for gazelles adjacent to the current location.
     * Only the first live gazelle is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        if (rand.nextDouble() < SUCCESSFUL_HUNT_PROB) {
            super.findFood(prey);
        }
        return null;
    }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected int getMaxAge() { return MAX_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected Animal createOffspring(Field field, Location location)
    {
        return new Lion(false, field, location);
    }

     /**
     * @return The food value of a lion
     */
    public int getFoodValue()
    { 
        return LION_FOOD_VALUE;
    }

    /**
     * @return The probability of a lion successfully hunting its prey
     */
    public double getHuntProb(){
        return SUCCESSFUL_HUNT_PROB;
    }
    
    /**
     * @return The list of prey which it eats 
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }
}