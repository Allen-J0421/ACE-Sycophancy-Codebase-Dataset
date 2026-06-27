import java.util.Random;

/**
 * Represents a disease that may infect an animal. The disease progresses
 * over a number of steps and may prove fatal once it has run its course.
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
