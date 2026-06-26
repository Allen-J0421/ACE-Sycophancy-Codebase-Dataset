/**
 * Common behavior for anything that can occupy a field location.
 */
public interface FieldOccupant
{
    /**
     * Check whether the occupant is alive.
     */
    boolean isAlive();

    /**
     * Mark the occupant as dead and remove it from the field.
     */
    void setDead();
}
