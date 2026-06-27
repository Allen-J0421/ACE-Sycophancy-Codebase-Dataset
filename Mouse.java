import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a mouse.
 * Mice age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal
{
    // Characteristics shared by all Mice (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The food value of eating a single mouse
    private static final int MOUSE_FOOD_VALUE = 10;
    // The maximum number of births
    private static final int MAX_LITTER_SIZE = 4;
    // The probability of the animal successfully hunting their food
    private final double SUCCESSFUL_HUNT_PROB = 0.7;
    // A list of the prey that the mouse eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass"));

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(MOUSE_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newMice A list to return newly born mice.
     */
    public void act(List<Actor> newMice)
    {
        super.act(newMice);
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first piece of grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(ArrayList<String> preyList)
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
        return new Mouse(false, field, location);
    }

     /**
     * @return the food value of a mouse, which a predator gains if
     * the mouse is eaten
     */
    public int getFoodValue()
    { 
        return MOUSE_FOOD_VALUE;
    }
    
    /**
     * @return The list of prey which it eats 
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }
}
