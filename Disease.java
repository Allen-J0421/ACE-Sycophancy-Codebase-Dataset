import java.util.Random;

/**
 * Represent a location in a rectangular grid.
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
        if (rand.nextDouble() <= getCHANCE_OF_DEATH())
            return true;
        else
            return false;
    }
    
    /**
     * @return The chance of death from this disease.
     */
    protected double getCHANCE_OF_DEATH()
    {
        return CHANCE_OF_DEATH;
    }
    
    /**
     * @return True if this disease has run its course and finished.
     */
    public boolean diseaseFinished()
    {
        return !(STEPS_BEFORE_DEATH >= 1);
    }
    
    /**
     * Decrement the number of steps before death.
     */
    public void decrementStepsBeforeDeath()
    {   
        STEPS_BEFORE_DEATH--;
    }
}
