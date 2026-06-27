import java.util.Random;

/**
 * The biological sex used by breeding animals in the simulation.
 */
public enum Gender
{
    MALE,
    FEMALE;

    /**
     * Pick a random gender.
     */
    public static Gender random(Random rand)
    {
        if(rand.nextBoolean()) {
            return MALE;
        }
        else {
            return FEMALE;
        }
    }
}
