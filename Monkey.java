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

    // The age at which a monkey can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a monkey can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a monkey breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The max health of a monkey
    private static final int MAX_HEALTH = 10;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    // The food a monkey will eat.
    private static final HashSet<Class<?>> FOOD_SOURCES = createClassSet(Plant.class);
    // The classes a monkey will kill
    private static final HashSet<Class<?>> KILLABLE = createClassSet(Plant.class);

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
    
    // Accessor and mutator methods
    /**
     * Return whether the monkey is diurnal
     * 
     * @return boolean True if the monkey is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Return the breeding age of the monkey
     * 
     * @return the monkey's breeding age
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the max age of the monkey
     * 
     * @return the monkey's max age
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return the breeding probability of the monkey
     * 
     * @return the monkey's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the max litter size of the monkey
     * 
     * @return the monkey's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the max health of the monkey
     * 
     * @return the monkey's max health
     */
    protected int getMaxHealth()
    {
        return MAX_HEALTH;
    }
    
    /**
     * Returns the HashSet of allowed food for a monkey to eat
     * 
     * @return HashSet<Class> of subclasses that a Monkey can eat
     */
    protected HashSet<Class<?>> getFoodSources() {
        return FOOD_SOURCES;
    }
    
    /**
     * Returns the HashSet of allowed classes for a monkey to kill
     * 
     * @return HashSet<Class> of subclasses that a Monkey can kill
     */
    protected HashSet<Class<?>> getKillable() {
        return KILLABLE;
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
