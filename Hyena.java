import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a hyena.
 * Hyenas age, move, eat gazelles, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Hyena extends Animal
{
    // Characteristics shared by all hyenas (class variables).
    
    // The age at which a hyena can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a hyena can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.60;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The probability of the animal successfully hunting their food
    private final double SUCCESSFUL_HUNT_PROB = 0.63;
    // The food value of a hyena - not eaten by any predators so initial
    // food value of newly generated hyenas
    private static final int HYENA_FOOD_VALUE = 15;
    // A list of the prey that the hyena eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Fennec Fox", "Gazelle"));

    /**
     * Create a hyena. A hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the hyena will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(HYENA_FOOD_VALUE);
        }
    }

    /**
     * This is what the hyena does most of the time: it hunts for
     * gazelles. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newHyenas A list to return newly born hyenas.
     */
    public void act(List<Actor> newHyenas)
    {
        super.act(newHyenas);
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
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
        return new Hyena(false, field, location);
    }

     /**
     * @return the food value of a hyena
     */
    public int getFoodValue()
    { 
        return HYENA_FOOD_VALUE;
    }

    /**
     * @return the probability of a hyena successfully hunting its prey
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
