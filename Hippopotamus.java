import java.util.Set;

/**
 * A model of a hippopotamus. Hippopotamus will eat monkeys and plants,
 * but will kill monkeys, plants and bears.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Hippopotamus extends Animal
{
    // Characteristics shared by all hippopotamuses (class variables).
    
    private static final AnimalProfile PROFILE = new AnimalProfile(25, 120, 0.12, 3, 100, true);
    
    // Individual characteristics (instance fields).
    
    // The food a hippopotamus will eat.
    private static final Set<Class<?>> FOOD_SOURCES = createClassSet(Monkey.class, Plant.class);
    // The classes a hippopotamus will kill
    private static final Set<Class<?>> KILLABLE = createClassSet(Monkey.class, Plant.class, Bear.class);

    /**
     * Create a hippopotamus. A hippopotamus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the hippopotamus will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hippopotamus(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    @Override
    protected AnimalProfile getProfile()
    {
        return PROFILE;
    }
    
    /**
     * Returns the HashSet of allowed food for a hippopotamus to eat
     * 
     * @return Set<Class<?>> of subclasses that a hippopotamus can eat
     */
    @Override
    protected Set<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a hippo to kill
     * 
     * @return Set<Class<?>> of subclasses that a hippopotamus can kill
     */
    @Override
    protected Set<Class<?>> getKillable() {
        return KILLABLE;
    }
    
    /**
     * Creates and returns a new hippopotamus object
     * 
     * @return Organism object of subclass hippopotamus
     */
    @Override
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Hippopotamus(randomAge, field, location);
    }
}
