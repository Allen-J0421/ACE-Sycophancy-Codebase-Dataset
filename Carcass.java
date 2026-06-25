import java.util.Random;
import java.util.List;
/**
 * A simple model of a carcass.
 *
 * @version 27.02.22 (DD:MM:YY)
 */
class Carcass extends Actor
{   
    //the carcass may be diseased if its left for too long:
    private boolean diseased;
    // Number of steps before the corpse becomes infected:
    private int STEPS_BEFORE_INFECTION;
    /**
     * Create a new carcass at a location in the field.
     * 
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the actor if consumed.
     */
    Carcass(Field field,Location loc,int consumptionWorth)
    {   
        super(field,loc,consumptionWorth,0,0,0,1);
        STEPS_BEFORE_INFECTION = rand.nextInt(15);
    }
    /**
     * Decrements the number of steps for the carcass to be infected every step in the simulator.
     */
    private void decrementStepsBeforeInfection()
    {
        STEPS_BEFORE_INFECTION--;
    }
    /**
     * Sets the Carcass to be diseased(any consumer to eat it will be diseased as well)
     */
    private void makeDiseased()
    {
        if(STEPS_BEFORE_INFECTION == 0)
        {
            diseased = true;
        }
    }
    /**
     * Make this carcass act - that is: make it do
     * whatever it needs to do.
     * 
     * @param newCarcasses A list to receive newly born consumers.
     */
    public void act(List<Actor> newCarcasses)
    {
        decrementStepsBeforeInfection();
        makeDiseased();
    }
    /**
     * Returns true if the carcass is diseased and false if it isn't.
     * return if the carcass is diseased
     */
    public boolean isDiseased()
    {
        return this.diseased;
    }
    /**
     * Returns true if the actor can become a carcass and false if it can't.
     * return if the actor can become a carcass.
     */
    protected boolean becomeCarcass()
    {
        return false;
    }
}