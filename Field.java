import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single actor, either an animal or plant.
 *
 *
 * @version 27.02.22
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    private static final int MAX_ALTITUDE = 5;
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals, plants, weather and altitude.
    private Object[][] animalField;
    private Object[][] plantField;
    private Map<String, double[][]> weatherField;
    private int[][] altitudeField;
    private  int waterLevel;
    private static final int maxWaterLevel = 3;
    private static final int minWaterLevel = 1;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        animalField = new Object[depth][width];
        plantField = new Object[depth][width];
        weatherField = createWeatherFieldMap(depth, width);
        altitudeField = new int[depth][width];
        waterLevel = 3;
    }

    /**
     * Create a Map with a key of the weather condition attribute name
     * and a value of weather attribute.
     * @param depth The depth of the field.
     * @param width The width of the field.
     * @return The weather field Map.
     */
    private Map<String, double[][]> createWeatherFieldMap(int depth, int width)
    {
        Map<String, double[][]> mapTemp = new HashMap<>();
        WeatherCond weatherTemp = WeatherCond.Sunny;
        for (String weatherAttribute : weatherTemp.getWeatherAttributesMap().keySet()) {
            mapTemp.put(weatherAttribute, new double[depth][width]);
        }
        return mapTemp;
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                animalField[row][col] = null;
                plantField[row][col] = null;
                for (String weatherAttribute : weatherField.keySet()) {
                    weatherField.get(weatherAttribute)[row][col] = 0;
                }
                altitudeField[row][col] = MAX_ALTITUDE;
            }
        }
        generateTerrain();
    }

    /**
     * Clear the given location.
     * The type of actor is specified to clear the correct actor field.
     * @param actor The actor in the location being cleared.
     * @param location The location to clear.
     */
    public void clear(Actor actor, Location location)
    {
        if (actor instanceof Animal){
            animalField[location.getRow()][location.getCol()] = null;
        }
        else if (actor instanceof Plant){
            plantField[location.getRow()][location.getCol()] = null;
        }
    }

    /**
     * Place an actor at the given location.
     * If there is already an actor at the location it will
     * be lost.
     * @param actor The actor to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object actor, int row, int col)
    {
        place(actor, new Location(row, col));
    }

    /**
     * Place an actor at the given location.
     * If there is already an actor at the location it will
     * be lost.
     * @param actor The actor to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object actor, Location location)
    {
        if (actor instanceof Animal) {
            animalField[location.getRow()][location.getCol()] = actor;
        }
        else if (actor instanceof Plant) {
            plantField[location.getRow()][location.getCol()] = actor;
        }
    }

    /**
     * Return the actor at the given location, if any.
     * @param location Where in the field.
     * @return The actor at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the actor at the given location, if any.
     * Animals are prioritised over Plants, and are returned if both
     * are present in that location.
     * @param row The desired row.
     * @param col The desired column.
     * @return The actor at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        if (animalField[row][col] != null) {
            return animalField[row][col];
        }
        return plantField[row][col];
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
     * @param actor The actor finding free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Actor actor, Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);

        for(Location next : adjacent) {
            if ((getObjectAt(next) == null || (actor instanceof Animal && getObjectAt(next) instanceof Plant))
                    && canTraverse(actor, next)) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Check whether the given actor can move onto the location's terrain.
     * @param actor The actor moving.
     * @param location The target location.
     * @return True if the terrain is suitable for the actor.
     */
    public boolean canTraverse(Actor actor, Location location)
    {
        return canTraverse(actor, location.getRow(), location.getCol());
    }

    /**
     * Check whether an actor with the given movement traits can move onto a location.
     * @param canGoLand Whether the actor can move on land.
     * @param canGoWater Whether the actor can move on water.
     * @param location The target location.
     * @return True if the terrain is suitable.
     */
    public boolean canTraverse(boolean canGoLand, boolean canGoWater, Location location)
    {
        return canTraverse(canGoLand, canGoWater, location.getRow(), location.getCol());
    }

    /**
     * Check whether an actor with the given movement traits can move onto terrain.
     * @param canGoLand Whether the actor can move on land.
     * @param canGoWater Whether the actor can move on water.
     * @param row The target row.
     * @param col The target column.
     * @return True if the terrain is suitable.
     */
    public boolean canTraverse(boolean canGoLand, boolean canGoWater, int row, int col)
    {
        if(canGoLand && canGoWater) {
            return true;
        }
        if(isUnderWater(row, col)) {
            return canGoWater;
        }
        return canGoLand;
    }

    /**
     * Check whether the given actor can move onto the terrain at the given coordinates.
     * @param actor The actor moving.
     * @param row The target row.
     * @param col The target column.
     * @return True if the terrain is suitable for the actor.
     */
    public boolean canTraverse(Actor actor, int row, int col)
    {
        return canTraverse(actor.canMoveOnLand(), actor.canMoveOnWater(), row, col);
    }

    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param actor The actor finding a free adjacent location.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Actor actor, Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(actor, location);
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
     * Set the values of each weather attributes field uniformly
     * distributed around a specified average.
     * @param weather The weather condition.
     */
    public void setWeatherField(WeatherCond weather) 
    {
        for (String weatherAttribute : weatherField.keySet()) {
            double avgWeather = weather.getWeatherAttributesMap().get(weatherAttribute);
            for(int row = 0; row < depth; row++) {
                for(int col = 0; col < width; col++) {
                    weatherField.get(weatherAttribute)[row][col] = avgWeather + (rand.nextDouble() - 0.5) * 2 / 5;
                }
            }
        }

        if(weather.getDampness() > 0.7 && waterLevel < maxWaterLevel){
            waterLevel += 1;
        }

        else if (waterLevel> minWaterLevel){
            if(weather.getBrightness() > 0.9 && waterLevel > minWaterLevel + 1){
                waterLevel -=2;
            }
            else {
                waterLevel -= 1;
            }
        }
    }

    /**
     * Returns the weather attribute value at a specified location.
     * @param weatherAttribute The String name of the weather attribute.
     * @param loc The location.
     * @return The weather attribute value at the specified location.
     */
    public double getWeatherAttributeValueAt(String weatherAttribute, Location loc)
    {
        return weatherField.get(weatherAttribute)[loc.getRow()][loc.getCol()];
    }

    /**
     * Returns whether or not the location with the specified row and column
     * is under water.
     * @param row The desired row.
     * @param col The desired column.
     * @return Whether or not the location is under water.
     */
    public boolean isUnderWater(int row, int col)
    {
        return altitudeField[row][col] <= waterLevel;
    }

    /**
     * Randomly generates altitude numbers in the altitude field.
     */
    public void generateTerrain()
    {
        Location centre = new Location(rand.nextInt(depth), rand.nextInt(width));
        boolean horizontal = rand.nextBoolean();
        clearTerrainLine(centre, horizontal);
        int stop = 0;
        int x = 0;
        int stopLimit = horizontal ? depth / 6 : width / 9;
        while (stop < stopLimit) {
            x++;
            stop += growTerrainBand(centre, x, horizontal);
        }
    }

    /**
     * Clear the initial terrain line used as the seed for generation.
     * @param centre The line centre.
     * @param horizontal True to clear a row, false to clear a column.
     */
    private void clearTerrainLine(Location centre, boolean horizontal)
    {
        if(horizontal) {
            for (int col = 0; col < width; col++) {
                altitudeField[centre.getRow()][col] = 0;
            }
        }
        else {
            for (int row = 0; row < depth; row++) {
                altitudeField[row][centre.getCol()] = 0;
            }
        }
    }

    /**
     * Grow the terrain around the seed line.
     * @param centre The line centre.
     * @param offset How far to expand from the seed line.
     * @param horizontal True if the seed line is horizontal.
     * @return How many cells were raised to max altitude.
     */
    private int growTerrainBand(Location centre, int offset, boolean horizontal)
    {
        int stop = 0;
        Location topOrLeft = new Location(centre.getRow() - offset, centre.getCol() - offset);
        stop += incrementAltitude(topOrLeft.getRow(), topOrLeft.getCol(), topOrLeft.getRow() + 1, topOrLeft.getCol() + 1);
        Location bottomOrRight = new Location(centre.getRow() + offset, centre.getCol() + offset);
        stop += incrementAltitude(bottomOrRight.getRow(), bottomOrRight.getCol(), bottomOrRight.getRow() - 1, bottomOrRight.getCol() - 1);

        if(horizontal) {
            for (int col = 0; col < width; col++) {
                stop += incrementAltitude(topOrLeft.getRow(), col, topOrLeft.getRow() + 1, col);
                stop += incrementAltitude(bottomOrRight.getRow(), col, bottomOrRight.getRow() - 1, col);
            }
        }
        else {
            for (int row = 0; row < width; row++) {
                stop += incrementAltitude(row, topOrLeft.getCol(), row, topOrLeft.getCol() + 1);
                stop += incrementAltitude(row, bottomOrRight.getCol(), row, bottomOrRight.getCol() - 1);
            }
        }
        return stop;
    }
    
    private int incrementAltitude(int row, int col, int adjRow, int adjCol)
    {
        if (row < depth && col < width && adjRow < depth && adjCol < width && row > 0 && col > 0 && adjRow > 0 && adjCol > 0) {
            int newAltitude;
            int altitudeChance = Math.max(1, depth * width / 1000);
            if (rand.nextInt(altitudeChance) == 0) {
                newAltitude = altitudeField[adjRow][adjCol] + 1;
            }
            else {
                newAltitude = altitudeField[adjRow][adjCol];
            }
            if (newAltitude >= MAX_ALTITUDE) {
                altitudeField[row][col] = MAX_ALTITUDE;
            }
            else if (newAltitude <= 0) {
                altitudeField[row][col] = 0;
            }
            else {
                altitudeField[row][col] = newAltitude;
            }
            if (altitudeField[row][col] == MAX_ALTITUDE) {
                return 1;
            }
        }
        return 0;
    }
}
