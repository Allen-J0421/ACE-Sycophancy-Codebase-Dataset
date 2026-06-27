import java.util.Set;

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

    private static final AnimalProfile PROFILE = new AnimalProfile(4, 40, 0.17, 5, 10, true);
    
    // Individual characteristics (instance fields).
    // The food a monkey will eat.
    private static final Set<Class<?>> FOOD_SOURCES = createClassSet(Plant.class);
    // The classes a monkey will kill
    private static final Set<Class<?>> KILLABLE = createClassSet(Plant.class);

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
        super(randomAge, field, location);
    }
    
    @Override
    protected AnimalProfile getProfile()
    {
        return PROFILE;
    }
    
    /**
     * Returns the HashSet of allowed food for a monkey to eat
     * 
     * @return Set<Class<?>> of subclasses that a Monkey can eat
     */
    @Override
    protected Set<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a monkey to kill
     * 
     * @return Set<Class<?>> of subclasses that a Monkey can kill
     */
    @Override
    protected Set<Class<?>> getKillable() {
        return KILLABLE;
    }
    
    /**
     * Creates a new Animal object for a newborn monkey
     * 
     * @return an Animal object for a newborn monkey
     */
    @Override
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Monkey(randomAge, field, location);
    }
}
