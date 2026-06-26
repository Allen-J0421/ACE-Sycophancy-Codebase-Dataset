/**
 * Mutable movement-related state for an organism.
 *
 * @version 26/02/2022
 */
public class MovementState
{
    private double movementProbability;

    public double getMovementProbability()
    {
        return movementProbability;
    }

    public void setMovementProbability(double movementProbability)
    {
        this.movementProbability = movementProbability;
    }
}
