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

    /**
     * Create a new disease with its own progression timer.
     */
    public Disease()
    {
        stepsBeforeDeath = rand.nextInt(MAX_STEPS_BEFORE_DEATH) + 1;
    }
    
    /**
     * @return True if disease killed animal.
     */
    public boolean isFatal()
    {
        return rand.nextDouble() <= getChanceOfDeath();
    }
    
    /**
     * @return The chance of death from this disease.
     */
    protected double getChanceOfDeath()
    {
        return CHANCE_OF_DEATH;
    }
    
    /**
     * @return True if this disease has run its course and finished.
     */
    public boolean diseaseFinished()
    {
        return stepsBeforeDeath == 0;
    }
    
    /**
     * Decrement the number of steps before death.
     */
    public void decrementStepsBeforeDeath()
    {   
        if (stepsBeforeDeath > 0)
        {
            stepsBeforeDeath--;
        }
    }
}
