import java.util.HashSet;

/**
 * A model of a leopard. Leopards will eat sloths,
 * and interact with nearby organisms through feeding and infection.
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
                Leopard.class, FOOD_SOURCES);
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
