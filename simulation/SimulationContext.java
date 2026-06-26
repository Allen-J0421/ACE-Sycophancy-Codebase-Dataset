package simulation;

import java.util.List;

/**
 * Read/query and event interface exposed to simulation entities.
 */
public interface SimulationContext
{
    List<Location> adjacentLocations(Location location);

    List<Location> getFreeAdjacentLocations(Location location);

    Location freeAdjacentLocation(Location location);

    Object getObjectAt(Location location);

    void emit(SimulationEvent event);
}
