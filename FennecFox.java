import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a fennec fox.
 * Fennec foxes age, move, eat mice, and die.
 *
 * @version 2016.02.29 (2)
 */
public class FennecFox extends Animal
{
    // Characteristics shared by all fennec foxes (class variables).
    
    // The age at which a fennec fox can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a fennec fox can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a fennec fox breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The probability of the animal successfully hunting their food
    private final double SUCCESSFUL_HUNT_PROB = 0.6;
    // The food value of a single fennec fox. In effect, this is the
    // number of steps a predator can go before it has to eat again.
    private static final int FENNECFOX_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom(); 
    // A list of the prey that the fennec fox eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass", "Mouse"));
    
    /**
     * Create a fennec fox. A fennec fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fennec fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public FennecFox(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(FENNECFOX_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the fennec fox does most of the time: it hunts for
     * mice. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Actor> newFoxes)
    {
        super.act(newFoxes);
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        if (rand.nextDouble() < SUCCESSFUL_HUNT_PROB) {
            super.findFood(prey);
        }
        return null;
    }
    
    /**
     * Check whether or not this fennec fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born fennec foxs.
     */
    protected List<Location> giveBirth(List<Actor> newFoxes)
    {
         Field field = getField();
        int births = breed();
        List<Location> free = super.giveBirth(newFoxes);
        if (free != null) {
            for(int b = 0; b < births && free.size() > 0; b++) {    
                Location loc = free.remove(0);
                FennecFox young = new FennecFox(false, field, loc);
                if (this.isInfected()) {
                    young.setInfected();
                }
                newFoxes.add(young);
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
     * A fennec fox can breed if it is female and has reached the breeding age.
     */
     private boolean canBreed()
    {
        return (this.isFemale() && this.getAge() >= BREEDING_AGE);
    }
    
     /**
     * @return the food value of a fennec fox, which a predator gains if 
     * the fennec fox is eaten
     */
    public int getFoodValue()
    { 
        return FENNECFOX_FOOD_VALUE;
    }
    
    /**
     * @return The list of prey which it eats 
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }

    /**
     * @return The maximum age of a fennec fox.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
}
