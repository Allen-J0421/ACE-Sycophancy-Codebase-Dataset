import java.util.HashSet;
import java.util.List;

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
    private static final int BREEDING_AGE = 15;
    private static final int MAX_AGE = 55;
    private static final double BREEDING_PROBABILITY = 0.145;
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_HEALTH = 40;
    private static final boolean IS_DIURNAL = false;

    private static final HashSet<Class> FOOD_SOURCES = classSet(Sloth.class);
    private static final HashSet<Class> KILLABLE = classSet(Sloth.class, Plant.class);

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE,
                BREEDING_PROBABILITY, MAX_LITTER_SIZE, IS_DIURNAL, MAX_HEALTH,
                Leopard.class, FOOD_SOURCES, KILLABLE);
    }

    /**
     * This is what the leopard does most of the time: it hunts for
     * sloths. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newLeopards A list to return newly born leopards.
     */
    public void act(List<Organism> newLeopards)
    {
        incrementAge();
        incrementHealth();
        if(isAlive()) {
            performStandardAct(newLeopards);
        }
    }

    /**
     * Creates and returns a new leopard object
     * 
     * @return Organism object of subclass leopard
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Leopard(randomAge, field, location);
    }
}
