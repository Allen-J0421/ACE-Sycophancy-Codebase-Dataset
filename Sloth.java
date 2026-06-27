import java.util.Set;

/**
 * A model of a Sloth. Sloths will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Sloth extends InfectiousAnimal
{
    // Characteristics shared by all sloths (class variables).

    // The age at which a sloth can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a sloth can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a sloth breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The max health of a sloth
    private static final int MAX_HEALTH = 8;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    // The food a sloth will eat.
    private static final Set<Class<?>> FOOD_SOURCES = classSet(Plant.class);
    // The classes a bear will kill
    private static final Set<Class<?>> KILLABLE = classSet(Plant.class);

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
        super(randomAge, field, location, Sloth.class, FOOD_SOURCES, KILLABLE);
    }
    
    // Accessor and mutator methods
    /**
     * Return whether the sloth is diurnal
     * 
     * @return boolean True if the sloth is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Return the breeding age of the sloth
     * 
     * @return the sloth's breeding age
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the max age of the sloth
     * 
     * @return the sloth's max age
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return the breeding probability of the sloth
     * 
     * @return the sloth's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the max litter size of the sloth
     * 
     * @return the sloth's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the max health of the sloth
     * 
     * @return the sloth's max health
     */
    protected int getMaxHealth()
    {
        return MAX_HEALTH;
    }
    
    /**
     * Creates a new Animal object for a newborn sloth
     * 
     * @return an Animal object for a newborn sloth
     */
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Sloth(randomAge, field, location);
    }
}
