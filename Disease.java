import java.util.Random;

/**
 * Represents a disease that can infect consumers in the simulation.
 * A disease runs for a random number of steps before resolving as
 * either fatal or non-fatal.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Disease
{
    // A shared random number generator:
    private static final Random rand = Randomizer.getRandom();
    // Constants representing configuration information for diseases:
    private static int STEPS_BEFORE_DEATH = rand.nextInt(20);
    private static final double CHANCE_OF_DEATH = 0.02;

    /**
     * @return True if the disease kills the host when it finishes.
     */
    public boolean isFatal()
    {
        return rand.nextDouble() <= CHANCE_OF_DEATH;
    }

    /**
     * @return True if this disease has run its course and finished.
     */
    public boolean diseaseFinished()
    {
        return STEPS_BEFORE_DEATH < 1;
    }

    /**
     * Decrement the number of steps before death.
     */
    public void decrementStepsBeforeDeath()
    {
        STEPS_BEFORE_DEATH--;
    }
}
