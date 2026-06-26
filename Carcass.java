import java.util.List;

/**
 * A simple model of a carcass.
 *
 * @version 27.02.22 (DD:MM:YY)
 */
class Carcass extends Actor
{   
    private static final int MAX_STEPS_BEFORE_INFECTION = 15;

    // The carcass may become diseased if it is left for too long:
    private boolean diseased;
    // Number of steps before the corpse becomes infected:
    private int stepsBeforeInfection;

    /**
     * Create a new carcass at a location in the field.
     * 
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the actor if consumed.
     */
    Carcass(Field field, Location location, int consumptionWorth)
    {   
        super(field, location, consumptionWorth, 0, 0, 0, 1);
        stepsBeforeInfection = rand.nextInt(MAX_STEPS_BEFORE_INFECTION);
    }

    /**
     * Decrements the number of steps for the carcass to be infected every step in the simulator.
     */
    private void decrementStepsBeforeInfection()
    {
        stepsBeforeInfection--;
    }

    /**
     * Sets the Carcass to be diseased(any consumer to eat it will be diseased as well)
     */
    private void makeDiseased()
    {
        if (stepsBeforeInfection == 0)
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
        return diseased;
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
