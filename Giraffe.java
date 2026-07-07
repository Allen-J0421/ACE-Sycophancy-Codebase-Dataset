/**
 * A simple model of a Giraffe.
 * Giraffes age, move, breed, eat plants, and die.
 *
 * @version 25/02/2022
 */
public class Giraffe extends Prey
{
    public static final int    BREEDING_AGE         = 30;
    public static final int    MAX_AGE              = 500;
    public static final double BREEDING_PROBABILITY = 0.65;
    public static final int    MAX_LITTER_SIZE      = 2;
    public static final int    MAX_FOOD_LEVEL       = 20;
    public static final int    FOOD_VALUE           = 15;

    /**
     * Create a new Giraffe. A Giraffe may be created as a newborn or with a random age.
     *
     * @param randomAge  If true, the giraffe will have a random age.
     * @param field      The field currently occupied.
     * @param location   The location within the field.
     * @param isInfected Whether the animal starts infected.
     * @param isImmune   Whether the animal starts immune.
     */
    public Giraffe(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
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
            foodLevel = (int) (0.25 * MAX_FOOD_LEVEL);
        }
    }
}
