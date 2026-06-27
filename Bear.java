import java.util.HashSet;

/**
 * A model of a bear. Bears will eat monkeys,
 * but will kill monkeys and plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Bear extends Animal implements Infectable
{
    // Characteristics shared by all bears (class variables).
    
    private static final AnimalProfile PROFILE = new AnimalProfile(15, 70, 0.125, 4, 40, true);
    
    // Individual characteristics (instance fields).
    
    // The food a bear will eat.
    private static final HashSet<Class<?>> FOOD_SOURCES = createClassSet(Monkey.class);
    // The classes a bear will kill
    private static final HashSet<Class<?>> KILLABLE = createClassSet(Monkey.class, Plant.class);

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    @Override
    protected AnimalProfile getProfile()
    {
        return PROFILE;
    }
    
    /**
     * Returns the allowed food for a bear to eat
     * 
     * @return HashSet<Class<?>> of subclasses that a bear can eat
     */
    @Override
    protected HashSet<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a bear to kill
     * 
     * @return HashSet<Class<?>> of subclasses that a bear can kill
     */
    @Override
    protected HashSet<Class<?>> getKillable() {
        return KILLABLE;
    }
    
    /**
     * Creates and returns a new bear object
     * 
     * @return Organism object of subclass bear
     */
    @Override
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Bear(randomAge, field, location);
    }
}
