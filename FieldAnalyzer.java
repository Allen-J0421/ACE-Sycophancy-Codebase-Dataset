import java.util.*;

/**
 * Provides spatial query and analysis operations over a Field.
 * Field itself stores raw grid data; this class answers questions
 * such as "what is adjacent?", "which neighbours are free?", and
 * "where are candidate grass patches?".
 *
 * @version 2022.03.02
 */
public class FieldAnalyzer
{
    private static final Random rand = Randomizer.getRandom();

    private final Field field;

    public FieldAnalyzer(Field field)
    {
        this.field = field;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < field.getDepth()) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        if(nextCol >= 0 && nextCol < field.getWidth() && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            // Shuffle the list. Several other methods rely on the list being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Generate a random location that is adjacent to the given location.
     * The returned location will be within the valid bounds of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return adjacentLocations(location).get(0);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        for(Location next : adjacentLocations(location)) {
            if(field.getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location adjacent to the given location.
     * @param location The location from which to generate an adjacency.
     * @return A free adjacent location, or null if there is none.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.size() > 0 ? free.get(0) : null;
    }

    /**
     * Return adjacent locations that contain an object of the given class.
     * @param location The location of an organism.
     * @param speciesClass The type of species.
     * @return A list of adjacent locations occupied by the specified species.
     */
    public List<Location> adjacentLocationsWithSpecies(Location location, Class<?> speciesClass)
    {
        List<Location> result = new ArrayList<>();
        for(Location loc : adjacentLocations(location)) {
            Object obj = field.getObjectAt(loc);
            if(obj != null && obj.getClass() == speciesClass) {
                result.add(loc);
            }
        }
        return result;
    }

    /**
     * Return all "free patch" centre locations — cells where both the cell and
     * all its neighbours are empty. Patches do not overlap.
     * @return The list of free patch centres in the field.
     */
    public List<Location> getFreePatches()
    {
        List<Location> result = new ArrayList<>();
        for(int i = 0; i < field.getDepth(); i++) {
            for(int j = 0; j < field.getWidth(); j++) {
                if(field.getObjectAt(i, j) == null) {
                    Location centre = new Location(i, j);
                    boolean allNull = true;
                    for(Location nearby : adjacentLocations(centre)) {
                        if(field.getObjectAt(nearby) != null) {
                            allNull = false;
                            break;
                        }
                    }
                    if(allNull) {
                        result.add(centre);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a random subset of free patches, sized by the given generation rate.
     * Adjacent duplicates are removed to prevent clustering.
     * @param rateOfGeneration Fraction of free patches to sample.
     * @return A list of sampled patch-centre locations.
     */
    public List<Location> getRandomFreePatches(double rateOfGeneration)
    {
        List<Location> patches = getFreePatches();
        List<Location> selected = new ArrayList<>();
        int count = (int)(rateOfGeneration * patches.size());
        for(int i = 0; i < count; i++) {
            if(patches.isEmpty()) break;
            Location patch = patches.get(rand.nextInt(patches.size()));
            selected.add(patch);
            for(Location loc : adjacentLocations(patch)) {
                patches.remove(loc);
            }
        }
        return selected;
    }
}
