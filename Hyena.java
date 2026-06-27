import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

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
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A list of the prey that the hyena eats
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Fennec Fox", "Gazelle"));

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
    @Override
    public void act(List<Actor> newHyenas)
    {
        super.act(newHyenas);
    }

    /**
     * Increase the age. This could result in the hyena's death.
     */
    @Override
    public void incrementAge() {
        super.incrementAge();    
        if(this.getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this hyena is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newhyenas A list to return newly born hyenas.
     */
    @Override
    protected List<Location> giveBirth(List<Actor> newHyenas)
    {
         Field field = getField();
        int births = breed();
        List<Location> free = super.giveBirth(newHyenas);
        if (free != null) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Hyena young = new Hyena(false, field, loc);
                if (this.isInfected()) {
                    young.setInfected();
                }
                newHyenas.add(young);
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
     * A hyena can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return (this.isFemale() && this.getAge() >= BREEDING_AGE);
    }

    /**
     * @return the food value of a hyena 
     */
    @Override
    public int getFoodValue()
    { 
        return HYENA_FOOD_VALUE;
    }

    /**
     * @return The probability of a hyena successfully hunting its prey.
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
