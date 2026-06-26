import java.util.*;

/**
 * A simple model of a hunter. 
 * Hunters hunt all of the species in the simulation.
 * @version 2022.03.02
 */
public class Hunter extends Organism
{
    private static final TargetAcquisitionPolicy TARGET_POLICY = new HunterTargetAcquisitionPolicy();


    /**
     * Creates a new hunter.
     * @param field The field currently occupied.
     * @param location  The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Makes the hunter hunt for nearby animals. 
     * @param environment The environment that the hunter resides in. 
     * @param newActors A list to receive new actors. 
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if(isAlive()) {
            forageAndMove(environment);
        }
    }

    @Override
    protected Location locateTargetLocation(Environment environment)
    {
        return TARGET_POLICY.acquireTarget(this, environment);
    }

    @Override
    protected void onMovementBlocked()
    {
        // Hunters stay put if they cannot move.
    }

}
