package safari;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position can store one blocking actor and one traversable actor.
 *
 * @version 2016.02.29
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private final int depth, width;
    // Storage for blocking actors, such as animals and hunters.
    private final Actor[][] occupants;
    // Storage for traversable actors, such as grass.
    private final Actor[][] traversableActors;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        occupants = new Actor[depth][width];
        traversableActors = new Actor[depth][width];
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                occupants[row][col] = null;
                traversableActors[row][col] = null;
            }
        }
    }
    
    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        occupants[location.getRow()][location.getCol()] = null;
        traversableActors[location.getRow()][location.getCol()] = null;
    }

    /**
     * Clear the given actor from the field.
     * @param actor The actor to clear.
     */
    public void clear(Actor actor)
    {
        if(actor == null) {
            return;
        }
        Location location = actor.getLocation();
        if(location == null) {
            return;
        }
        if(actor.isTraversable()) {
            traversableActors[location.getRow()][location.getCol()] = null;
        }
        else {
            occupants[location.getRow()][location.getCol()] = null;
        }
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Actor actor, int row, int col)
    {
        place(actor, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Actor actor, Location location)
    {
        if(actor == null || location == null) {
            return;
        }
        if(actor.isTraversable()) {
            traversableActors[location.getRow()][location.getCol()] = actor;
        }
        else {
            occupants[location.getRow()][location.getCol()] = actor;
        }
    }
    
    /**
     * Return the visible actor at the given location, if any.
     * @param location Where in the field.
     * @return The actor at the given location, or null if there is none.
     */
    public Actor getActorAt(Location location)
    {
        return getActorAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the visible actor at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The actor at the given location, or null if there is none.
     */
    public Actor getActorAt(int row, int col)
    {
        if(occupants[row][col] != null) {
            return occupants[row][col];
        }
        return traversableActors[row][col];
    }

    /**
     * Return the blocking actor at the given location, if any.
     * @param location Where in the field.
     * @return The blocking actor at the location, or null.
     */
    public Actor getOccupantAt(Location location)
    {
        return occupants[location.getRow()][location.getCol()];
    }

    /**
     * Return the traversable actor at the given location, if any.
     * @param location Where in the field.
     * @return The traversable actor at the location, or null.
     */
    public Actor getTraversableActorAt(Location location)
    {
        return traversableActors[location.getRow()][location.getCol()];
    }

    /**
     * Determine whether a location has a blocking occupant.
     * @param location The location to inspect.
     * @return true if the location is blocked by an occupant.
     */
    public boolean isOccupied(Location location)
    {
        return getOccupantAt(location) != null;
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getOccupantAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Get a shuffled list of the free adjacent locations for traversable actors.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations for ground-cover actors.
     */
    public List<Location> getFreeAdjacentTraversableLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getTraversableActorAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
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
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Return whether a location can accept a new traversable actor.
     * @param location The location to check.
     * @return true if no traversable actor is present.
     */
    public boolean isFreeForTraversableActor(Location location)
    {
        return getTraversableActorAt(location) == null;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
