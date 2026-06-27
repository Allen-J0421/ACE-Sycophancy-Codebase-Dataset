import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.EnumMap;
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
    private enum TerrainOrientation
    {
        HORIZONTAL,
        VERTICAL
    }

    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    private static final int MAX_ALTITUDE = 5;
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals, plants, weather and altitude.
    private Actor[][] animalField;
    private Actor[][] plantField;
    private Map<WeatherAttribute, double[][]> weatherField;
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
        animalField = new Actor[depth][width];
        plantField = new Actor[depth][width];
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
    private Map<WeatherAttribute, double[][]> createWeatherFieldMap(int depth, int width)
    {
        Map<WeatherAttribute, double[][]> mapTemp = new EnumMap<>(WeatherAttribute.class);
        for (WeatherAttribute weatherAttribute : WeatherAttribute.values()) {
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
                clearCell(row, col);
            }
        }
        generateTerrain();
    }

    /**
     * Clear one field cell across actors, weather, and altitude.
     * @param row The row to clear.
     * @param col The column to clear.
     */
    private void clearCell(int row, int col)
    {
        animalField[row][col] = null;
        plantField[row][col] = null;
        for (WeatherAttribute weatherAttribute : weatherField.keySet()) {
            weatherField.get(weatherAttribute)[row][col] = 0;
        }
        altitudeField[row][col] = MAX_ALTITUDE;
    }

    /**
     * Clear the given location.
     * The type of actor is specified to clear the correct actor field.
     * @param actor The actor in the location being cleared.
     * @param location The location to clear.
     */
    public void clear(Actor actor, Location location)
    {
        actorGridFor(actor)[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an actor at the given location.
     * If there is already an actor at the location it will
     * be lost.
     * @param actor The actor to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Actor actor, int row, int col)
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
    public void place(Actor actor, Location location)
    {
        actorGridFor(actor)[location.getRow()][location.getCol()] = actor;
    }

    /**
     * Return the backing grid used for the given actor type.
     * @param actor The actor whose grid is required.
     * @return The grid for the actor type.
     */
    private Actor[][] actorGridFor(Actor actor)
    {
        if(actor instanceof Animal) {
            return animalField;
        }
        return plantField;
    }

    /**
     * Return the actor at the given location, if any.
     * @param location Where in the field.
     * @return The actor at the given location, or null if there is none.
     */
    public Actor getActorAt(Location location)
    {
        return getActorAt(location.getRow(), location.getCol());
    }

    /**
     * Return the actor at the given location, if any.
     * Animals are prioritised over Plants, and are returned if both
     * are present in that location.
     * @param row The desired row.
     * @param col The desired column.
     * @return The actor at the given location, or null if there is none.
     */
    public Actor getActorAt(int row, int col)
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
            Actor occupant = getActorAt(next);
            if((occupant == null || (actor instanceof Animal && occupant instanceof Plant))
               && canOccupy(actor, next)) {
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
     * @param actor The actor finding a free adjacent location.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Actor actor, Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(actor, location);
        return free.size() > 0 ? free.get(0) : null;
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
        for (WeatherAttribute weatherAttribute : weatherField.keySet()) {
            fillWeatherAttribute(weatherAttribute, weather.getAttribute(weatherAttribute));
        }
        updateWaterLevel(weather);
    }

    /**
     * Fill a weather attribute field with values centered around an average.
     * @param weatherAttribute The attribute field to fill.
     * @param averageValue The average attribute value.
     */
    private void fillWeatherAttribute(WeatherAttribute weatherAttribute, double averageValue)
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                weatherField.get(weatherAttribute)[row][col] = averageValue + (rand.nextDouble() - 0.5) * 2 / 5;
            }
        }
    }

    /**
     * Update the field water level based on the current weather.
     * @param weather The current weather condition.
     */
    private void updateWaterLevel(WeatherCond weather)
    {
        if(shouldIncreaseWaterLevel(weather)) {
            waterLevel += 1;
        }
        else if(shouldDecreaseWaterLevel()) {
            waterLevel -= getWaterLevelDecrease(weather);
        }
    }

    /**
     * Return whether the current weather should raise the water level.
     * @param weather The current weather condition.
     * @return true if the water level should increase.
     */
    private boolean shouldIncreaseWaterLevel(WeatherCond weather)
    {
        return weather.getDampness() > 0.7 && waterLevel < maxWaterLevel;
    }

    /**
     * Return whether the current water level may decrease.
     * @return true if the water level may decrease.
     */
    private boolean shouldDecreaseWaterLevel()
    {
        return waterLevel > minWaterLevel;
    }

    /**
     * Return how much the water level should drop for the current weather.
     * @param weather The current weather condition.
     * @return The water level decrease.
     */
    private int getWaterLevelDecrease(WeatherCond weather)
    {
        if(weather.getBrightness() > 0.9 && waterLevel > minWaterLevel + 1) {
            return 2;
        }
        return 1;
    }

    /**
     * Returns the weather attribute value at a specified location.
     * @param weatherAttribute The String name of the weather attribute.
     * @param loc The location.
     * @return The weather attribute value at the specified location.
     */
    public double getWeatherAttributeValueAt(WeatherAttribute weatherAttribute, Location loc)
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
     * Returns whether the given actor can occupy the specified location.
     * @param actor The actor trying to occupy the location.
     * @param location The location being checked.
     * @return true if the actor can survive at the location.
     */
    public boolean canOccupy(Actor actor, Location location)
    {
        if(actor == null || location == null) {
            return false;
        }

        return canOccupy(actor.canMoveOnLand(), actor.canMoveOnWater(), location);
    }

    /**
     * Returns whether an actor with the given movement capabilities can occupy
     * the specified location.
     * @param canGoLand Whether the actor can survive on land.
     * @param canGoWater Whether the actor can survive in water.
     * @param location The location being checked.
     * @return true if the actor can survive at the location.
     */
    public boolean canOccupy(boolean canGoLand, boolean canGoWater, Location location)
    {
        if(location == null) {
            return false;
        }

        if(canGoLand && canGoWater) {
            return true;
        }

        boolean underWater = isUnderWater(location.getRow(), location.getCol());
        return (underWater && canGoWater)
            || (!underWater && canGoLand);
    }

    /**
     * Randomly generates altitude numbers in the altitude field.
     */
    public void generateTerrain()
    {
        Location centre = new Location(rand.nextInt(depth), rand.nextInt(width));
        TerrainOrientation orientation = rand.nextBoolean()
            ? TerrainOrientation.HORIZONTAL : TerrainOrientation.VERTICAL;
        initializeWaterChannel(centre, orientation);
        expandTerrainFrom(centre, orientation);
    }

    /**
     * Initialize the central zero-altitude water channel.
     * @param centre The centre of the generated terrain.
     * @param orientation The orientation of the channel.
     */
    private void initializeWaterChannel(Location centre, TerrainOrientation orientation)
    {
        if(orientation == TerrainOrientation.HORIZONTAL) {
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
     * Expand terrain away from the central water channel.
     * @param centre The centre of the generated terrain.
     * @param orientation The orientation of the channel.
     */
    private void expandTerrainFrom(Location centre, TerrainOrientation orientation)
    {
        int stop = 0;
        int offset = 0;
        int stopLimit = orientation == TerrainOrientation.HORIZONTAL ? depth / 6 : width / 9;

        while (stop < stopLimit) {
            offset++;
            Location topOrLeft = new Location(centre.getRow() - offset, centre.getCol() - offset);
            Location bottomOrRight = new Location(centre.getRow() + offset, centre.getCol() + offset);
            stop += incrementAltitude(topOrLeft.getRow(), topOrLeft.getCol(),
                topOrLeft.getRow() + 1, topOrLeft.getCol() + 1);
            stop += incrementAltitude(bottomOrRight.getRow(), bottomOrRight.getCol(),
                bottomOrRight.getRow() - 1, bottomOrRight.getCol() - 1);
            stop += extendTerrainBand(topOrLeft, bottomOrRight, orientation);
        }
    }

    /**
     * Extend one pair of terrain bands for the given orientation.
     * @param topOrLeft The upper or left band location.
     * @param bottomOrRight The lower or right band location.
     * @param orientation The orientation being expanded.
     * @return The number of cells that reached maximum altitude.
     */
    private int extendTerrainBand(Location topOrLeft, Location bottomOrRight, TerrainOrientation orientation)
    {
        int stop = 0;
        int span = orientation == TerrainOrientation.HORIZONTAL ? width : depth;

        for (int index = 0; index < span; index++) {
            if(orientation == TerrainOrientation.HORIZONTAL) {
                stop += incrementAltitude(topOrLeft.getRow(), index, topOrLeft.getRow() + 1, index);
                stop += incrementAltitude(bottomOrRight.getRow(), index, bottomOrRight.getRow() - 1, index);
            }
            else {
                stop += incrementAltitude(index, topOrLeft.getCol(), index, topOrLeft.getCol() + 1);
                stop += incrementAltitude(index, bottomOrRight.getCol(), index, bottomOrRight.getCol() - 1);
            }
        }
        return stop;
    }

    private int incrementAltitude(int row, int col, int adjRow, int adjCol)
    {
        if (isInterior(row, col) && isInterior(adjRow, adjCol)) {
            int newAltitude = altitudeField[adjRow][adjCol];
            if (rand.nextInt(depth * width / 1000) == 0) {
                newAltitude++;
            }
            altitudeField[row][col] = clampAltitude(newAltitude);
            return altitudeField[row][col] == MAX_ALTITUDE ? 1 : 0;
        }
        return 0;
    }

    /**
     * Return whether the given position lies within the interior terrain bounds.
     * @param row The row to test.
     * @param col The column to test.
     * @return true if the position is within the interior.
     */
    private boolean isInterior(int row, int col)
    {
        return row > 0 && row < depth && col > 0 && col < width;
    }

    /**
     * Clamp an altitude into the valid terrain range.
     * @param altitude The proposed altitude.
     * @return The clamped altitude.
     */
    private int clampAltitude(int altitude)
    {
        if (altitude >= MAX_ALTITUDE) {
            return MAX_ALTITUDE;
        }
        if (altitude <= 0) {
            return 0;
        }
        return altitude;
    }
}
