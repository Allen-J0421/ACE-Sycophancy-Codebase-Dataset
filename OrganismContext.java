/**
 * Shared runtime context needed by organism behavior mixins.
 *
 * @version 26/02/2022
 */
public interface OrganismContext
{
    Field currentField();

    Location currentLocation();

    boolean organismIsAlive();

    void markDead();
}
