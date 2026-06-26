import java.util.Random;

/**
 * Mixin for organisms that move through the field.
 *
 * @version 26/02/2022
 */
public interface Movable extends OrganismContext
{
    Random RANDOM = Randomizer.getRandom();

    MovementState getMovementState();

    Location locateFoodSource();

    void relocate(Location newLocation);

    default void moveOneStep()
    {
        Location newLocation = locateFoodSource();

        if(newLocation == null) 
        { 
            Location possibleNewLocation = currentField().freeAdjacentLocation(currentLocation(), Animal.class);

            if (possibleNewLocation == null) 
            {
                if (RANDOM.nextDouble() < 0.3) 
                {
                    markDead();
                }
            }

            if (RANDOM.nextDouble() <= getMovementState().getMovementProbability()) 
            {
                newLocation = possibleNewLocation;
            }
        }

        if(newLocation != null && organismIsAlive())
        { 
            relocate(newLocation);
        }
    }
}
