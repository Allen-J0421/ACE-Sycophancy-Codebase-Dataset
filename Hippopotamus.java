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
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a hippopotamus can start to breed.
    private static final int BREEDING_AGE = 25;
    // The age to which a hippopotamus can live.
    private static final int MAX_AGE = 120;
    // The likelihood of a hippopotamus breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The max health of a hippopotamus
    private static final int MAX_HEALTH = 100;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    
    // The food a hippopotamus will eat.
    private static final Set<Class<?>> FOOD_SOURCES = classSet(Monkey.class, Plant.class);
    // The classes a hippopotamus will kill
    private static final Set<Class<?>> KILLABLE = classSet(Monkey.class, Plant.class, Bear.class);

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hippopotamus(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, Hippopotamus.class, FOOD_SOURCES, KILLABLE);
    }
    
    // Accessor and mutator methods
    
    // Accessor and mutator methods
    /**
     * Return whether the hippopotamus is diurnal
     * 
     * @return boolean True if the bea is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Returns breeding age of a hippopotamus
     * 
     * @return int of the hippopotamus's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a hippopotamus
     * 
     * @return int of the hippopotamus's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Returns breeding probability of a hippopotamus
     * 
     * @return double of the hippopotamus's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns max litter size of a hippopotamus
     * 
     * @return int of the hippopotamus's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the max health of the animal
     * 
     * @return the animal's max health
     */
    protected int getMaxHealth()
    {
        return MAX_HEALTH;
    }
    
    /**
     * Creates and returns a new hippopotamus object
     * 
     * @return Organism object of subclass hippopotamus
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Hippopotamus(randomAge, field, location);
    }
}
