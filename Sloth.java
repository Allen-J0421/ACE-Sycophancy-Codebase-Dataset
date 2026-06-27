import java.util.HashSet;

/**
 * A model of a Sloth. Sloths will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Sloth extends Animal implements Infectable
{
    // Characteristics shared by all sloths (class variables).

    private static final AnimalProfile PROFILE = new AnimalProfile(5, 30, 0.17, 4, 8, true);
    
    // Individual characteristics (instance fields).
    // The food a sloth will eat.
    private static final HashSet<Class<?>> FOOD_SOURCES = createClassSet(Plant.class);
    // The classes a sloth will kill
    private static final HashSet<Class<?>> KILLABLE = createClassSet(Plant.class);

    /**
     * Create a new sloth. A sloth may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the sloth will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Sloth(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    @Override
    protected AnimalProfile getProfile()
    {
        return PROFILE;
    }
    
    /**
     * Returns the HashSet of allowed food for a sloth to eat
     * 
     * @return HashSet<Class<?>> of subclasses that a Sloth can eat
     */
    @Override
    protected HashSet<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a sloth to eat
     * 
     * @return HashSet<Class<?>> of subclasses that a sloth can kill
     */
    @Override
    protected HashSet<Class<?>> getKillable() {
        return KILLABLE;
    }
    
    /**
     * Creates a new Animal object for a newborn sloth
     * 
     * @return an Animal object for a newborn sloth
     */
    @Override
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Sloth(randomAge, field, location);
    }
}
