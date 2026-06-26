import java.util.Random;

/**
 * Represents a disease affecting a consumer.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Disease
{
    // A shared random number generator:
    private static final Random rand = Randomizer.getRandom();
    // Constants representing configuration information for diseases:
    private static final int MAX_STEPS_BEFORE_DEATH = 20;
    private static final double CHANCE_OF_DEATH = 0.02;

    private int stepsBeforeDeath;

    public Disease()
    {
        stepsBeforeDeath = rand.nextInt(MAX_STEPS_BEFORE_DEATH);
    }
    
    /**
     * @return True if disease killed animal.
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
        return stepsBeforeDeath < 1;
    }
    
    /**
     * Decrement the number of steps before death.
     */
    public void decrementStepsBeforeDeath()
    {   
        stepsBeforeDeath--;
    }
}
