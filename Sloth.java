import java.util.HashSet;
import java.util.List;

/**
 * A model of a Sloth. Sloths will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Sloth extends InfectableAnimal
{
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 30;
    private static final double BREEDING_PROBABILITY = 0.17;
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_HEALTH = 8;
    private static final boolean IS_DIURNAL = true;

    private static final HashSet<Class> FOOD_SOURCES = classSet(Plant.class);

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
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE,
                BREEDING_PROBABILITY, MAX_LITTER_SIZE, IS_DIURNAL, MAX_HEALTH,
                Sloth.class, FOOD_SOURCES);
    }

    /**
     * This is what the sloth does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newsloths A list to return newly born sloths.
     */
    public void act(List<Organism> newsloths)
    {
        incrementAge();
        incrementHealth();
        if(isAlive()) {
            performInfectableAct(newsloths);
        }
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
