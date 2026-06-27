import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a gazelle
 * Gazelles age, move, find water, find food(grass), find mate, breed, and die.
 *
 * @version 2022.02.27
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all gazelles (class variables).

    // The age at which a gazelle can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a gazelle can live.
    private static final int MAX_AGE = 45;
    // The likelihood of a gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The food value of a gazelle that predators get when they eat it.
    // In effect, the number of steps a predator can go before they need to eat again
    private static final int GAZELLE_FOOD_VALUE = 20;
    // The maximum number of births a gazelle can give.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A list of the food that the gazelle eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass"));

    /**
     * Create a new gazelle. A gazelle may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.setAge(0);
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(GAZELLE_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the gazelle does most of the time - it runs 
     * around and eats grass. Sometimes it will breed or die of old age.
     * @param newGazelles A list to return newly born gazelles.
     */
    public void act(List<Actor> newGazelles)
    {
        super.act(newGazelles);
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first piece of grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        super.findFood(prey);
        return null;
    }

    /**
     * Check whether or not this gazelle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGazelles A list to return newly born Gazelles.
     */
    protected List<Location> giveBirth(List<Actor> newGazelles)
    {
        Field field = getField();
        int births = breed();
        List<Location> free = super.giveBirth(newGazelles);
        if (free != null) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Gazelle young = new Gazelle(false, field, loc);
                // If this animal is infected with a disease, passes on to young
                if (this.isInfected()) {
                    young.setInfected();
                }
                newGazelles.add(young);
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
     * A gazelle can breed if it is female and has reached the breeding age.
     * @return true if the gazelle can breed, false otherwise.
     */
     private boolean canBreed()
    {
        return (this.isFemale() && this.getAge() >= BREEDING_AGE);
    }
    
    /**
     * @return the food value of a gazelle, which a predator gains if 
     * the gazelle is eaten
     */
    public int getFoodValue()
    { 
        return GAZELLE_FOOD_VALUE;
    }
    
    /**
     * @return The list of prey which it eats 
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }

    /**
     * @return The maximum age of a gazelle.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
}
