import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single creature.
 *
 * @version 2022/03/02
 */
public class Field
{
    @FunctionalInterface
    public interface FieldVisitor
    {
        void visit(Location location, Creature creature);
    }

    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the creatures.
    private Creature[][] field;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Creature[depth][width];
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        forEachLocation((location, creature) -> clear(location));
    }
    
    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }
    
    /**
     * Place a creature at the given location.
     * If there is already a creature at the location it will
     * be lost.
     * @param creature The creature to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Creature creature, int row, int col)
    {
        place(creature, new Location(row, col));
    }
    
    /**
     * Place an creature at the given location.
     * If there is already an creature at the location it will
     * be lost.
     * @param creature The creature to be placed.
     * @param location Where to place the creature.
     */
    public void place(Creature creature, Location location)
    {
        field[location.getRow()][location.getCol()] = creature;
    }
    
    /**
     * Return the creature at the given location, if any.
     * @param location Where in the field.
     * @return The creature at the given location, or null if there is none.
     */
    public Creature getCreatureAt(Location location)
    {
        return getCreatureAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the creature at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The creature at the given location, or null if there is none.
     */
    public Creature getCreatureAt(int row, int col)
    {
        return field[row][col];
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
        List<Location> adjacent = adjacentLocations(location, 1);
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
        List<Location> adjacent = adjacentLocations(location, 1);
        for(Location next : adjacent) {
            if(getCreatureAt(next) == null) {
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
     * The list will INCLUDE the location itself.
     * All locations will lie within the grid.
     * 
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocationsIncludingSelf(Location location, int adjacentDistance)
    {
        return adjacentLocations(location, adjacentDistance, true);
    }
    
    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location, int adjacentDistance)
    {
        return adjacentLocations(location, adjacentDistance, false);
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
    /**
     * Generate a random location from the field.
     * Using randomWidth and randomDepth to create an instance of Location.
     * @return Location a random location.
     */
     public Location generateRandomLocation()
    {
       int randomRow = rand.nextInt(depth);
       int randomCol = rand.nextInt(width);
       
       return new Location(randomRow, randomCol);
         
    }
    /**
     * Create a list and holds all objects that is adjacent to this specific location.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance used to Controls the range to which this method can search in the field.
     * @return a list of adjacent objects.
     */
    public List<Creature> getCreaturesAt(Location location, int adjacentDistance)
    {
        return getCreaturesAt(adjacentLocationsIncludingSelf(location, adjacentDistance));
    }

    /**
     * Visit every location in the field.
     */
    public void forEachLocation(FieldVisitor visitor)
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                Location location = new Location(row, col);
                visitor.visit(location, getCreatureAt(location));
            }
        }
    }

    private List<Location> adjacentLocations(Location location, int adjacentDistance, boolean includeSelf)
    {
        assert location != null : "Null location passed to adjacentLocations";

        List<Location> locations = new LinkedList<>();
        if(location == null) {
            return locations;
        }

        int row = location.getRow();
        int col = location.getCol();
        for(int rowOffset = -adjacentDistance; rowOffset <= adjacentDistance; rowOffset++) {
            int nextRow = row + rowOffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int colOffset = -adjacentDistance; colOffset <= adjacentDistance; colOffset++) {
                    int nextCol = col + colOffset;
                    if(isValidColumn(nextCol) && (includeSelf || rowOffset != 0 || colOffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }

        Collections.shuffle(locations, rand);
        return locations;
    }

    private List<Creature> getCreaturesAt(List<Location> locations)
    {
        List<Creature> creatures = new ArrayList<>();
        for(Location location : locations) {
            Creature creature = getCreatureAt(location);
            if(creature != null) {
                creatures.add(creature);
            }
        }
        return creatures;
    }

    private boolean isValidColumn(int column)
    {
        return column >= 0 && column < width;
    }
}
