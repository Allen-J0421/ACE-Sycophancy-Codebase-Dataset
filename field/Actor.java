package field;

import java.util.List;

/**
 * Represents an Entity which can have an action on each step, and can die.
 *
 */
public abstract class Actor extends Entity {
    /**
     * Constructor for Actor
     * @param ID the ID of the actor
     * @param location the location of the actor
     * @param rgbColour the RGB colour of the actor
     */
    public Actor(String ID, Location location, int[] rgbColour) {
        super(ID, location, rgbColour);
    }

    /**
     * The action of this actor on each step.
     * @param newActors where to add new actors in the event of birth.
     */
    public abstract void act(List<Actor> newActors);

    /**
     * How the actor should handle death.
     */
    public abstract void die();
}
