package field;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Field acts as a data model for storing all Blocks.
 * It also manages the current state of the day.
 * Each day has a variable length night.
 * It is implemented using the Singleton design pattern.
 *
 */
public class Field {
    private Block[][] field;

    private static final int DEFAULT_ROWS = 50;
    private static final int DEFAULT_COLS = 50;

    private int TIME_IN_DAY = 90;
    private int MIN_DAY_LENGTH = 60, MAX_DAY_LENGTH = 80;

    private final int rows;
    private final int cols;

    private Random rand;
    private int step;
    private int dayLengthCounter, fullDayCounter;
    private DayState dayState;
    
    private static Field instance;

    /**
     * Set the dimensions of the instance. Can only be done if the instance
     * has not been initialised yet.
     * @param rows the number of rows of the field
     * @param cols the numbers of columns of the field
     */
    public static void setDimensions(int rows, int cols) {
        if (instance != null) {
            System.out.println("ERROR: Changing dimensions of an initialized field!");
            return;
        }
        instance = new Field(rows, cols);
    }

    /**
     * @return the instance of Field
     */
    public static Field getInstance() {
        if (instance == null) {
            instance = new Field();
        }

        return instance;
    }

    /**
     * Constructor for Field.
     * @param rows no of rows of the field
     * @param cols no of cols of the field
     */
    private Field(int rows, int cols) {
        rand = new Random();
        
        // adapted from
        // https://www.baeldung.com/java-generating-random-numbers-in-range
        dayLengthCounter = rand.nextInt(MAX_DAY_LENGTH - MIN_DAY_LENGTH) + MIN_DAY_LENGTH;
        fullDayCounter = TIME_IN_DAY;

        dayState = DayState.DAY;
        
        step = 0;
        this.rows = rows; 
        this.cols = cols;
        field = new Block[rows][cols];

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                field[i][j] = new Block(0, 0);
            }
        }
    }

    /**
     * Default constructor
     */
    private Field() {
        this(DEFAULT_ROWS, DEFAULT_COLS);
    }

    /**
     * Set each block to initial state and set step to 0.
     */
    public void reset() {
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                field[i][j] = new Block(0, 0);
            }
        }

        step = 0;
    }

    /* Setters */

    /**
     * Set the Entity in Block located at row, col to entity
     */
    public void setObjectAt(int row, int col, Entity entity) {
        field[row][col].setEntity(entity);
    }

    /**
     * Set the Entity in Block located at location to entity
     */
    public void setObjectAt(Location location, Entity entity) {
        field[location.getRow()][location.getCol()].setEntity(entity);
    }

    /**
     * Increment the step counter and update state of day if necessary.
     */
    public void incrementStep() {
        step++;
        dayLengthCounter--;
        fullDayCounter--;

        if (dayLengthCounter == 0) {
            dayState = DayState.NIGHT;
        } 
        
        if (fullDayCounter == 0) {
            dayState = DayState.DAY;
            
            dayLengthCounter = rand.nextInt(MAX_DAY_LENGTH - MIN_DAY_LENGTH) + MIN_DAY_LENGTH;
            fullDayCounter = TIME_IN_DAY;
        }
    }

    /* Getters */ 

    public int getRows() { 
        return rows; 
    }

    public int getCols() { 
        return cols; 
    }

    public int getStep() {
        return step;
    }

    public DayState getDayState() {
        return dayState;
    }

    public Block getBlockAt(int row, int col) {
        return field[row][col];
    }

    public Block getBlockAt(Location location) {
        return field[location.getRow()][location.getCol()];
    }

    /**
     * Check if row, col is in bounds
     */
    public boolean inBounds(int row, int col) {
        return !(row < 0 || col < 0 || row >= rows || col >= cols);
    }

     /**
     * Check if location is in bounds
     */
    public boolean inBounds(Location location) {
        int row = location.getRow(),
            col = location.getCol();
            
        return !(row < 0 || col < 0 || row >= rows || col >= cols);
    }

    /**
     * Get a list of adjacent locations
     * @param location the center location
     * @return a list containing Location objects which surround the center location
     */
    public List<Location> getAdjacentLocations(Location location) {
        int[] dRow = new int[]{ 1, -1, 0, 0, 1, -1, 1, -1 };
        int[] dCol = new int[]{ 0, 0, 1, -1, 1, -1, -1, 1 };

        List<Location> locations = new ArrayList<>();

        for (int i=0; i<dRow.length; i++) {
            int adj_row = location.getRow() + dRow[i],
                adj_col = location.getCol() + dCol[i];

            if (inBounds(adj_row, adj_col)) {
                locations.add(new Location(adj_row, adj_col));
            }
        }

        return locations;
    }
}
