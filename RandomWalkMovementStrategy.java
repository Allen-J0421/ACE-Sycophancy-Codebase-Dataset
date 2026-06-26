/**
 * Movement strategy that ignores food tracking and moves to a free adjacent location.
 */
public class RandomWalkMovementStrategy extends AbstractMovementStrategy
{
    @Override
    public void move(Animal animal, Environment environment)
    {
        moveOrHandleBlocked(animal, animal.getField().freeAdjacentLocation(animal.getLocation()));
    }
}
