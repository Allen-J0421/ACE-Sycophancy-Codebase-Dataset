import java.util.HashSet;

/**
 * A model of a hippopotamus. Hippopotamus will eat monkeys and plants,
 * and interact with nearby organisms through feeding and infection.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Hippopotamus extends Animal
{
    private static final int BREEDING_AGE = 25;
    private static final int MAX_AGE = 120;
    private static final double BREEDING_PROBABILITY = 0.12;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int MAX_HEALTH = 100;
    private static final boolean IS_DIURNAL = true;

    private static final HashSet<Class> FOOD_SOURCES = classSet(Monkey.class, Plant.class);

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
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE,
                BREEDING_PROBABILITY, MAX_LITTER_SIZE, IS_DIURNAL, MAX_HEALTH,
                Hippopotamus.class, FOOD_SOURCES);
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
