import java.util.*;

/**
 * A simple model of a eagle.
 * Eagles age, move,contract diseases, eat prey, and die.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
     // Characteristics shared by all eagles (class variables).

    // The age at which a eagle can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a eagle can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The eagles's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 14;
    // A shared random number generator to control breeding.
    // The food value of a single eagle.
    private static final int FOOD_VALUE = 5;
    // A set of organisms that a eagle consumes
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(Deer.class, Coyote.class, Mouse.class));

    // Implementing abstract methods to return fields to be used by the superclass
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxAge() { return MAX_AGE; }
    protected int getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
    protected int getFoodValue() { return FOOD_VALUE; }
    protected Set<Class<? extends Organism>> getDiet() { return DIET; }

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field.getRandomProvider(), field, location, randomAge, sex);
        this.isNocturnal = false;
    }

    
    /**
     * Check whether this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newEagles A list to return newly born Eagles.
     * @param environment The environment that the eagle resides in. 
     */
    protected Animal createYoung(Field field, Location location, Gender sex)
    {
        return new Eagle(false, field, location, sex);
    }

    /**
     * Additional functionality that doesn't allow eagles to find food while it's raining
     * @param environment The environment that the eagle resides in. 
     * @return Location Where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood(Environment environment)
    {
        if(environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return super.findFood(environment);
        }
        return null;
    }
}
