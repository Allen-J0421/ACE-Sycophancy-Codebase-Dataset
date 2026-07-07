/**
 * A simple model of a Lemur.
 * Lemurs age, move, breed, eat plants, and die.
 *
 * @version 25/02/2022
 */
public class Lemur extends Prey
{
    public static final int    BREEDING_AGE         = 15;
    public static final int    MAX_AGE              = 60;
    public static final double BREEDING_PROBABILITY = 0.4;
    public static final int    MAX_LITTER_SIZE      = 5;
    public static final int    MAX_FOOD_LEVEL       = 7;
    public static final int    FOOD_VALUE           = 10;

    /**
     * Create a new Lemur. A Lemur may be created as a newborn or with a random age.
     *
     * @param randomAge  If true, the lemur will have a random age.
     * @param field      The field currently occupied.
     * @param location   The location within the field.
     * @param isInfected Whether the animal starts infected.
     * @param isImmune   Whether the animal starts immune.
     */
    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune,
              new StandardReproduction(BREEDING_AGE, MAX_LITTER_SIZE, BREEDING_PROBABILITY));

        maxAge       = MAX_AGE;
        maxFoodLevel = MAX_FOOD_LEVEL;
        foodValue    = FOOD_VALUE;

        if (randomAge)
        {
            age       = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        }
        else
        {
            age       = 0;
            foodLevel = (int) (0.5 * MAX_FOOD_LEVEL);
        }
    }
}
