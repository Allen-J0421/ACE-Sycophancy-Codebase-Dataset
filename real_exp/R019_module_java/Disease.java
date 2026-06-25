
/**
 * A disease that an organism can transmit and die to.
 *
 * @version 2022.03.02
 */
public abstract class Disease
{
    //Probability for how easily the disease spreads
    protected float contagiousness;
    //how lethal the disease is.
    protected float deathProbability;

    /**
     * Create a disease
     */
    public Disease()
    {
        //
    }
    
    /**
     * Return contagiousness
     * @Return contagiousness The probability for how easily the disease spreads
     */
    protected float getContagiousness()
    {
        return contagiousness;
    }
    
    /**
     * Return death probability
     * @Return deathProbability The probability for how lethal the disease is.
     */
    protected float getDeathProbability()
    {
        return deathProbability;
    }
}
