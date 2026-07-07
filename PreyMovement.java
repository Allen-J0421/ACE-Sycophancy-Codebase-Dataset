/**
 * Movement behaviour for prey animals: inactive at night, otherwise
 * moves with a fixed probability.
 */
public class PreyMovement implements MovementBehaviour
{
    private static final double MOVEMENT_PROBABILITY = 0.75;

    @Override
    public boolean canActThisStep(SimRandom rand)
    {
        return !Time.isNight();
    }

    @Override
    public double getMovementProbability()
    {
        return MOVEMENT_PROBABILITY;
    }
}
