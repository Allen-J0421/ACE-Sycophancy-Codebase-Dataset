package controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import field.Block;
import field.Field;
import field.Location;
import utils.BfsCallable;
import utils.Randomizer;

/**
 * Handles everything related to the temperature and water level of each block.
 * Implemented as a singleton.
 *
 */
public class WeatherController {
    private Field field;
    private Random random;
    private WeatherState weatherState;

    private int[][] baseLayer;
    private int[][] currentTemperature;
    private Integer[][] modifierLayer;

    // Magic
    private int COLD_TEMPERATURE, MODERATE_TEMPERATURE, HOT_TEMPERATURE;
    private int MAX_WATER_LEVEL;

    private int rows;
    private int cols;

    private static WeatherController instance;

    /**
     * Get the instance of WeatherController. Initialise it if it isn't already.
     * @return this instance.
     */
    public static WeatherController getInstance() {
        if (instance == null) {
            instance = new WeatherController();
        }
        return instance;
    }

    /**
     * Constructor for WeatherController.
     */
    private WeatherController() {
        field = Field.getInstance();
        weatherState = WeatherState.getInstance();
        random = Randomizer.getRandom();

        COLD_TEMPERATURE = 0;
        MODERATE_TEMPERATURE = 32;
        HOT_TEMPERATURE = 70;

        MAX_WATER_LEVEL = 200;

        rows = field.getRows();
        cols = field.getCols();

        /* 
            Initialise each rendering layer for temperature.
            - baseLayer stores the temperature that the simulation begins in.
            - currentTemperature is used to compute the rate of temperature change
            during transitions from different weather states e.g. Sunny to Rain, or Rain to Normal.
            - modiferLayer stores the temperature after an ability is used e.g. FreezeSurroundings. 
            This creates a temporary temperature zone which will return to the temperature stored
            in baseLayer. 
        */
        baseLayer = new int[rows][cols];
        currentTemperature = new int[rows][cols];
        modifierLayer = new Integer[rows][cols];

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                modifierLayer[i][j] = null;
            }
        }
    }

    /* Public methods */

    /**
     * Generate hot, cold and moderate zones randomly.
     */
    public void generateWeather() {
        int rows = field.getRows();
        int cols = field.getCols();

        int blocks = rows * cols;

        int blocks_cold, blocks_moderate, blocks_hot;
        double PROPORTION_COLD, PROPORTION_MODERATE;
        int COLD_ZONES, HOT_ZONES;

        PROPORTION_COLD = 0.1;
        PROPORTION_MODERATE = 0.8;

        COLD_ZONES = 1;
        HOT_ZONES = 1;

        blocks_cold = (int)(PROPORTION_COLD * blocks);
        blocks_moderate = (int)(PROPORTION_MODERATE * blocks);
        blocks_hot = blocks - blocks_cold - blocks_moderate;

        List<Location> zones = new ArrayList<>();

        while (zones.size() < COLD_ZONES + HOT_ZONES) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            Location location = new Location(row, col);
            
            if (!zones.contains(location)) {
                zones.add(location);
            }
        }
        
        boolean[][] visited = new boolean[rows][cols];

        // Using BFS to generate the zones.
        BfsCallable bfsCallable = new ProcessInitBlock();

        for (int i=0; i<COLD_ZONES; i++) {
            bfs(zones.get(i), visited, COLD_TEMPERATURE, (int)(blocks_cold/COLD_ZONES), true, bfsCallable);
        }

        for (int i=COLD_ZONES; i<zones.size(); i++) {
            bfs(zones.get(i), visited, HOT_TEMPERATURE, (int)(blocks_hot/HOT_ZONES), true, bfsCallable);
        }

        // Updating rest of the blocks.
        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getRows(); j++) {
                if (!visited[i][j]) {
                    bfs(new Location(i, j), visited, MODERATE_TEMPERATURE, blocks_moderate, false, bfsCallable);
                }
            }
        }

        // updating baseLayer and currentTemperature.
        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                baseLayer[row][col] = field.getBlockAt(row, col).getTemperature();
                currentTemperature[row][col] = field.getBlockAt(row, col).getTemperature();
            }
        }

    }

    /**
     * Randomly generate the water level in each block.
     */
    public void generateWater() {
        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) {
                setInitWaterLevel(i, j);
            }
        }
    }

    /**
     * Add a random amount of water to each block.
     */
    public void rain() {
        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) {
                int water = (int)(random.nextDouble() * 3 + field.getBlockAt(i, j).getWaterLevel());
                setWaterLevel(i, j, water);
            }
        }
    }

    /**
     * Generate a temperature zone. Used by abilities.
     * @param location The location of the center of that zone.
     * @param blocks How big should the zone be in terms of blocks.
     * @param temperature The temperature that the blocks should be set to.
     */
    public void generateAbilityZone(Location location, int blocks, int temperature) {
        BfsCallable bfsCallable = new ProcessModiferUpdate();
        boolean[][] visited = new boolean[modifierLayer.length][modifierLayer[0].length];
        bfs(location, visited, temperature, blocks, false, bfsCallable);
    }

    /**
     * Compute how the temperature in each block should change in response
     * to changing weather states and abilities used.
     */
    public void updateTemperature() {
        Weather state = weatherState.getWeather();

        int duration = weatherState.getDuration(),
            finalStep = weatherState.getFinalStep();

        //System.out.println(field.getStep() + " " + finalStep);

        /*  Once the current weather state has finished, store the current state of the field,
            Then, use this to compute the rate of change of the temperature in each block. */
        if (field.getStep() == finalStep-1) {
            //System.out.println("ending weather modifier");
            for (int i=0; i<field.getRows(); i++) {
                for (int j=0; j<field.getCols(); j++) {
                    currentTemperature[i][j] = field.getBlockAt(i, j).getTemperature();
                }
            }
        }

        // The rate at which the temperature in each block changes.
        double fraction = 1 - Math.min((1.3 * (finalStep - field.getStep()) / (double)duration), 1);

        if (state == Weather.RAIN) {
            rain();
        }

        // Layer 0 updates

        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) { 
                updateBlockTemperature(i, j, fraction, state, currentTemperature[i][j]);
                //block.setTemperature(baseLayer[i][j] + weatherModifier);
            }
        }

        // Layer 1 updates

        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) { 
                Block block = field.getBlockAt(i, j);
                Integer temperature = modifierLayer[i][j];
                
                if (temperature == null)
                    continue;

                block.setTemperature(temperature);

                // Revert the temperature back to base temperature
                modifierLayer[i][j] = temperature + (int)(0.1 * (baseLayer[i][j] - temperature));
                if (Math.abs(modifierLayer[i][j] - baseLayer[i][j]) < 10) {
                    modifierLayer[i][j] = null;
                    updateBlockTemperature(i, j, fraction, state, currentTemperature[i][j]);
                }
            }
        }
    }

    /**
     * Reset the state of temperature and water.
     * This will generate new random values.
     */
    public void reset() {
        baseLayer = new int[rows][cols];
        currentTemperature = new int[rows][cols];
        modifierLayer = new Integer[rows][cols];

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                modifierLayer[i][j] = null;
            }
        }

        generateWeather();
        generateWater();
    }

    /* Getters */

    /**
     * Getter of max water level.
     * @return the max water level in each block.
     */
    public int getMaxWaterLevel() {
        return MAX_WATER_LEVEL;
    }

    /* Implementatiom methods */
    
    /**
     * Set the temperature of the block closer to its target temperature.
     * @param i row of the block to be updated
     * @param j col of the block to be updated
     * @param fraction the rate of change of the block
     * @param state the current state of the weather e.g. Rain or Sunny.
     * @param startTemp the temperature of the block at the start of the weather state.
     */
    private void updateBlockTemperature(int i, int j, double fraction, Weather state, int startTemp) {
        int base = baseLayer[i][j];
        // Target temperature
        int target = base;

        if (state == Weather.RAIN) {
            int c = random.nextInt(7 - 2) + 2;
            target = base - c;
        }

        else if (state == Weather.SUNNY) {
            int c = random.nextInt(7 - 2) + 2;
            target = base + c;
        }

        Block block = field.getBlockAt(i, j);
        int newTemperature = (int)(startTemp + fraction * (target - startTemp));
        
        block.setTemperature(newTemperature);
    }

    /**
     * Set the initial water level of the blocks
     * @param row row of the block
     * @param col col of the block
     */
    private void setInitWaterLevel(int row, int col) {
        int water = (int)(100 + random.nextDouble() * 50);
        setWaterLevel(row, col, water);
    }

    /**
     * Set water level within possible bounds.
     * @param row row of the block
     * @param col col of the block
     * @param water the water to set the block's water level at
     */
    private void setWaterLevel(int row, int col, int water) {
        Block block = field.getBlockAt(row, col);
        block.setWaterLevel(Math.max(water, 0));
        block.setWaterLevel(Math.min(water, MAX_WATER_LEVEL));
    }

    /**
     * Checks whether the block being processed is valid and should be added to the search.
     * Auxilliary method for the breadth-first search algorithm. 
     * @param visited 2D array containing which blocks have been visited already
     * @param row row of the block being processed
     * @param col col of the block being processed
     * @return whether the block being processed should be added to the queue.
     */
    private boolean isValidBfs(boolean[][] visited, int row, int col) {
        if (!field.inBounds(row, col))
            return false;

        if (visited[row][col])
            return false;

        return true;
    }

    /**
     * Perform a breadth-first search to of a fixed number of blocks and process each block.
     * @param location The location of the start of the search.
     * @param visited a 2D array containing which blocks have been visited already.
     * @param temperature The mean temperature to which each block should be set to.
     * @param blocks The number of blocks to carry out the search for.
     * @param fade Whether the temperature in each block should approach the moderate temperature as the search gets further from the center.
     * @param process A Callback which will process each block.
     */
    private void bfs(Location location, boolean[][] visited, int temperature, int blocks, boolean fade, BfsCallable process) {
        int dRow[] = {  1, -1, 0, 0, 1, -1, 1, -1 };
        int dCol[] = {  0, 0, 1, -1, 1, -1, -1, 1 };

        Queue<Location> q = new LinkedList<>();
        q.add(location);
        visited[location.getRow()][location.getCol()] = true;

        int count = 1;
        while (!q.isEmpty()) {
            Location loc = q.peek();
            int row = loc.getRow();
            int col = loc.getCol();

            // - Processing the block -
            process.execute(row, col, temperature, count, blocks, fade);
            // - end -

            q.remove();
            count++;

            for (int i=0; i<dRow.length; i++) {
                int adj_row = row + dRow[i];
                int adj_col = col + dCol[i];

                if (isValidBfs(visited, adj_row, adj_col) && count <= blocks) {
                    q.add(new Location(adj_row, adj_col));
                    visited[adj_row][adj_col] = true;
                }
            }
        }
    }

    /**
     * Callback which handles the initialisation of the temperatures in each block.
     */
    private class ProcessInitBlock implements BfsCallable {
        /**
         * Compute the temperature of the block according to parameters.
         * @param row row of the block
         * @param col col of the block
         * @param visited a 2D array containing which blocks have been visited already.
         * @param temperature The mean temperature to which each block should be set to.
         * @param blocks The number of blocks to carry out the search for.
         * @param fade Whether the temperature in each block should approach the moderate temperature as the search gets further from the center.
         * @param process A Callback which will process each block.
         */
        @Override
        public void execute(int row, int col, int temperature, int count, int blocks, boolean fade) {
            Block b = field.getBlockAt(row, col);
            Double d;
            if (fade) {
                d = random.nextGaussian() * 2 + temperature + (0.72 * (double)count/blocks) * (MODERATE_TEMPERATURE - temperature);
            } else {
                d = random.nextGaussian() * 4 + temperature;
            }
            int t = d.intValue();
            b.setTemperature(t);
        }
    }

     /**
     * Callback which handles generating the temperature zones created by abilities.
     */
    private class ProcessModiferUpdate implements BfsCallable {
        /**
         * Compute the temperature of the block according to parameters.
         * @param row row of the block
         * @param col col of the block
         * @param visited a 2D array containing which blocks have been visited already.
         * @param temperature The mean temperature to which each block should be set to.
         * @param blocks The number of blocks to carry out the search for.
         * @param fade Whether the temperature in each block should approach the moderate temperature as the search gets further from the center.
         * @param process A Callback which will process each block.
         */
        @Override
        public void execute(int row, int col, int temperature, int count, int blocks, boolean fade) {
            Double d;
            if (fade) {
                d = random.nextGaussian() * 2 + temperature + (0.72 * (double)count/blocks) * (MODERATE_TEMPERATURE - temperature);
            } else {
                d = random.nextGaussian() * 4 + temperature;
            }
            int t = d.intValue();
            modifierLayer[row][col] = t;
        }
    }
}
