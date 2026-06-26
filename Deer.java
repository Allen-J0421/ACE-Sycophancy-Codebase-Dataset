import java.util.*;

/**
 * A simple model of a deer.
 * Deers age, move, breed,contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Deer extends Animal
{
    // Characteristics shared by all deers (class variables).

    // The age at which a deer can start to breed.
    protected static final int BREEDING_AGE = 5;
    // The age to which a deer can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a deer breeding.
    protected static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    protected static final int MAX_LITTER_SIZE = 2;
    // The deer's food level which is increased by eating grass.
    private static final int MAX_FOOD_LEVEL = 9;
    // The food value of a deer. 
    private static final int FOOD_VALUE = 4;
    // A set of organisms that a deer consumes
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(Grass.class));

    // Implementing abstract methods to return fields to be used by the superclass
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxAge() { return MAX_AGE; }
    protected int getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
    protected int getFoodValue() { return FOOD_VALUE; }
    protected Set<Class<? extends Organism>> getDiet() { return DIET; }



    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the deer.
     */
    public Deer(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field.getSimulationContext(), field, location, randomAge, sex);
        this.isNocturnal = false;
    }
    /**
     * Makes the deer stay awake regardless of the time of the day
     * Overrides the method in the Animal class that uses the isNocturnal field to determine if it is awake
     */
    @Override
    public boolean isAwake(Environment environment) {
        return true;
    }
}
