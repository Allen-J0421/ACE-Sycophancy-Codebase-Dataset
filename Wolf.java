import java.util.*;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat prey,contract diseases and die.
 *
 * @version 2022.03.02
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).

    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The wolf's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 15;
    // A shared random number generator to control breeding.
    // The food value of a single wolf.
    private static final int FOOD_VALUE = 6;
    // A set of organisms that a wolf consumes
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(Deer.class, Mouse.class, Coyote.class));

    // Implementing abstract methods to return fields to be used by the superclass
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxAge() { return MAX_AGE; }
    protected int getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
    protected int getFoodValue() { return FOOD_VALUE; }
    protected Set<Class<? extends Organism>> getDiet() { return DIET; }

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the wolf.
     */
    public Wolf(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field.getRandomProvider(), field, location, randomAge, sex);
        this.isNocturnal = true;
    }
}
