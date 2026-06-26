import java.util.function.Predicate;

/**
 * Shared adjacency scan helper used by target-acquisition policies.
 */
public final class AdjacentTargetSearch
{
    private AdjacentTargetSearch()
    {
    }

    public static Location findMatchingLocation(Field field, Location origin, Predicate<Object> matcher)
    {
        for(Location location : field.adjacentLocations(origin)) {
            Object occupant = field.getObjectAt(location);
            if(matcher.test(occupant)) {
                return location;
            }
        }
        return null;
    }
}
