import java.util.HashSet;

/**
 * A model of a monkey. Monkeys will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Monkey extends Animal
{
    // Characteristics shared by all monkeys (class variables).
    private static final int BREEDING_AGE = 4;
    private static final int MAX_AGE = 40;
    private static final double BREEDING_PROBABILITY = 0.17;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int MAX_HEALTH = 10;
    private static final boolean IS_DIURNAL = true;

    private static final HashSet<Class> FOOD_SOURCES = classSet(Plant.class);

    /**
     * Create a new monkey. A monkey may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the monkey will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Monkey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE,
                BREEDING_PROBABILITY, MAX_LITTER_SIZE, IS_DIURNAL, MAX_HEALTH,
                Monkey.class, FOOD_SOURCES);
    }

    /**
     * Creates a new Animal object for a newborn monkey
     * 
     * @return an Animal object for a newborn monkey
     */
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Monkey(randomAge, field, location);
    }
}
