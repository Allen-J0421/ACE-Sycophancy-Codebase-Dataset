/**
 * Movement strategy that first seeks food, then falls back to a free adjacent location.
 */
public class PredatorTrackingMovementStrategy extends AbstractMovementStrategy
{
    @Override
    public void move(Animal animal, Environment environment)
    {
        Location newLocation = animal.findFood(environment);
        if(newLocation == null) {
            newLocation = animal.getField().freeAdjacentLocation(animal.getLocation());
        }
        moveOrHandleBlocked(animal, newLocation);
    }
}
