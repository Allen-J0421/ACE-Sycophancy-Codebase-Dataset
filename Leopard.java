import java.util.Set;

/**
 * A model of a leopard. Leopards will eat sloths,
 * but will kill sloths and plants.
 * They will move and look for food, age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Leopard extends Animal
{
    // Characteristics shared by all leopards (class variables).
    
    private static final AnimalProfile PROFILE = new AnimalProfile(15, 55, 0.145, 4, 40, false);
    
    // Individual characteristics (instance fields).
    
    // The food a leopard will eat.
    private static final Set<Class<?>> FOOD_SOURCES = createClassSet(Sloth.class);
    // The classes a leopard will kill
    private static final Set<Class<?>> KILLABLE = createClassSet(Sloth.class, Plant.class);

    /**
     * Create a leopard. A leopard can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the leopard will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    @Override
    protected AnimalProfile getProfile()
    {
        return PROFILE;
    }
    
    /**
     * Returns the HashSet of allowed food for a leopard to eat
     * 
     * @return Set<Class<?>> of subclasses that a leopard can eat
     */
    @Override
    protected Set<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a leopard to kill
     * 
     * @return Set<Class<?>> of subclasses that a leopard can kill
     */
    @Override
    protected Set<Class<?>> getKillable() {
        return KILLABLE;
    }
    
    /**
     * Creates and returns a new leopard object
     * 
     * @return Organism object of subclass leopard
     */
    @Override
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Leopard(randomAge, field, location);
    }
}
