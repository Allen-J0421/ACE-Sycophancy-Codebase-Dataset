import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Shared movement template for organisms that search adjacent cells for a target,
 * move toward it when found, and otherwise fall back to free or grass-adjacent
 * locations.
 */
public abstract class MobileForager extends Organism
{
    protected static final Random rand = Randomizer.getRandom();

    public MobileForager(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Template method for target acquisition and movement.
     */
    protected final void forageAndMove()
    {
        Location targetLocation = locateTargetLocation();
        if(targetLocation == null) {
            targetLocation = getField().freeAdjacentLocation(getLocation());
        }

        if(targetLocation != null) {
            setLocation(targetLocation);
            return;
        }

        List<Location> fallbackLocations = getFallbackLocations();
        if(!fallbackLocations.isEmpty()) {
            getField().clear(getLocation());
            setLocation(fallbackLocations.get(rand.nextInt(fallbackLocations.size())));
            return;
        }

        onMovementBlocked();
    }

    /**
     * Find the resource or prey the organism is seeking.
     */
    protected abstract Location locateTargetLocation();

    /**
     * Hook for species-specific fallback movement targets.
     */
    protected List<Location> getFallbackLocations()
    {
        return getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);
    }

    /**
     * Hook for what to do when movement is impossible.
     */
    protected void onMovementBlocked()
    {
        setDead();
    }

    /**
     * Find the first adjacent location whose occupant matches the predicate.
     * Adjacent locations are already shuffled by the field, so the first match
     * preserves the existing random-selection behavior.
     */
    protected final Location findAdjacentLocationMatching(Predicate<Object> matcher)
    {
        for(Location location : getField().adjacentLocations(getLocation())) {
            Object occupant = getField().getObjectAt(location);
            if(matcher.test(occupant)) {
                return location;
            }
        }
        return null;
    }
}
