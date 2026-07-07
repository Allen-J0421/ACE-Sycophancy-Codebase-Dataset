/**
 * A simple model of a cheetah.
 * Cheetahs age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Cheetah extends Predator
{
    public static final double PREY_CATCHING_PROBABILITY = 0.6;
    public static final int    BREEDING_AGE              = 60;
    public static final int    MAX_AGE                   = 1000;
    public static final double BREEDING_PROBABILITY      = 0.7;
    public static final int    MAX_LITTER_SIZE           = 3;
    public static final int    MAX_FOOD_LEVEL            = 40;
    public static final int    FOOD_VALUE                = 16;

    /**
     * Create a cheetah. A cheetah can be created as a newborn (age zero)
     * or with a random age and food level.
     *
     * @param randomAge  If true, the cheetah will have a random age and hunger level.
     * @param field      The field currently occupied.
     * @param location   The location within the field.
     * @param isInfected Whether the animal starts infected.
     * @param isImmune   Whether the animal starts immune.
     */
    public Cheetah(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune,
              PREY_CATCHING_PROBABILITY,
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
            age = 0;
            double percentageOfMaxFoodLevel = rand.nextDouble() / 5.5;
            foodLevel = (int) (percentageOfMaxFoodLevel * MAX_FOOD_LEVEL);
        }
    }
}
