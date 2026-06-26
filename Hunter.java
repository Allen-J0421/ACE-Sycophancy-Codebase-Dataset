import java.util.*;

/**
 * A simple model of a hunter. 
 * Hunters hunt all of the species in the simulation.
 * @version 2022.03.02
 */
public class Hunter extends Organism implements Actor
{
    // A hunter hunts every species. 
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(
            Deer.class,
            Mouse.class,
            Wolf.class,
            Coyote.class,
            Eagle.class
    ));


    /**
     * Creates a new hunter.
     * @param field The field currently occupied.
     * @param location  The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field.getRandomProvider(), field, location);
    }

    @Override
    protected int getFoodValue()
    {
        return 0;
    }

    /**
     * Makes the hunter hunt for nearby animals. 
     * @param environment The environment that the hunter resides in. 
     * @param newActors A list to receive new actors. 
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if(isAlive()) {
            move();
        }
    }

    /**
     * Returns the species that hunters can target.
     */
    protected Set<Class<? extends Organism>> getDiet()
    {
        return DIET;
    }

    private void move()
    {
        Field field = getField();
        MovementService movementService = field.getMovementService();
        List<Location> adjacentLocations = movementService.getAdjacentLocations(field, getLocation());
        MovementService.MovementDecision movementDecision = movementService.resolveHunterMovement(this, adjacentLocations);

        if(movementDecision.consumedOrganism() != null) {
            movementDecision.consumedOrganism().setDead();
        }

        if(movementDecision.targetLocation() != null) {
            movementService.moveOrganism(this, movementDecision.targetLocation());
        }
    }
}
