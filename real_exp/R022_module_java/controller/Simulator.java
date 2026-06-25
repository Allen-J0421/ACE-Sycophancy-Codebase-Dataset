package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import field.Actor;
import field.Entity;
import field.Field;
import field.FieldStatistics;
import field.Location;
import models.animal.AnimalFactory;
import models.plant.PlantFactory;
import views.PieChart;
import views.SimulatorView;
import views.TemperatureView;
import views.WaterView;

/**
 * Driver of the simulation. Initializes views, provides methods to update views 
 * and provides methods to progress the simulation.
 *
 */
public class Simulator {

    // State and Misc.
    private Field field;
    private List<Actor> actors;
    private FieldStatistics fieldStatistics;
    private Random random;

    // Controllers
    private WeatherController weatherController;
    private WeatherState weatherState;
    private SimulatorInitializer simulatorInitializer;

    // Views
    private SimulatorView view;
    private TemperatureView temperatureView;    
    private WaterView waterView;
    private PieChart pieChart;

    /**
     * Constructor for Simulator. 
     * Initialize state variables, other controllers and views.
     * Populate the field and generate temperature & water.
     */
    public Simulator() {
        Field.setDimensions(100, 100);
        field = Field.getInstance();
        actors = new ArrayList<>();
        random = new Random();

        weatherController = WeatherController.getInstance();
        weatherState = WeatherState.getInstance();
        simulatorInitializer = new SimulatorInitializer(actors);
        fieldStatistics = FieldStatistics.getInstance();

        int rows = field.getRows(), 
            cols = field.getCols();

        view = new SimulatorView(rows, cols, this);
        temperatureView = new TemperatureView(rows, cols);
        waterView = new WaterView(rows, cols, weatherController.getMaxWaterLevel());
        pieChart = new PieChart();

        generateWeather();
        generateWater();
        simulatorInitializer.populate();
        fieldStatistics.update();
        updateGUI();

        //fieldStatistics.print();
        //printField();
    }

    /**
     * Update all views to reflect new state of simulation.
     */
    private void updateGUI() {
        view.showStatus();
        temperatureView.showStatus();
        waterView.showStatus();
        pieChart.showStatus();
    }

    /**
     * Output a 2D representation of the temperature in each block to STDOUT.
     */
    public void printField() {
        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) {
                System.out.print(field.getBlockAt(i, j).getTemperature() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Generate the temperature in each block and update corresponding view.
     */
    public void generateWeather() {
        weatherController.generateWeather();
        temperatureView.showStatus();
    }

    /**
     * Generate the water level in each block and update corresponding view.
     */
    public void generateWater() {
        weatherController.generateWater();
        waterView.showStatus();
    }

    /**
     * Instantiate 9 actors of type ID at a random location. Entities previously
     * at that location are killed.
     * @param id The ID of the Actor to be spawned in.
     */
    private void spawnActor(String id)  {
        int row = random.nextInt(field.getRows());
        int col = random.nextInt(field.getCols());
        Location location = new Location(row, col);

        Entity e = field.getBlockAt(location).getEntity();
        
        if (e != null) {
            e.die();
        }

        Actor a = AnimalFactory.get(id, location);
        if (a == null) {
            a = PlantFactory.get(id, location);
        }

        if (a == null) {
            System.out.println("Invalid actor");
            return;
        }

        actors.add(a);
        field.setObjectAt(location, a);

        List<Location> locations = field.getAdjacentLocations(location);
        
        for (Location l : locations) {
            a = AnimalFactory.get(id, l);
            if (a == null) {
                a = PlantFactory.get(id, l);
            }
            
            e = field.getBlockAt(location).getEntity();
        
            if (e != null) {
                e.die();
            }
            actors.add(a);
            field.setObjectAt(l, a);
        }
    }

    /**
     * Systematically add actors to the simulation to maintain a stable and diverse
     * simulation. 
     */
    private void addActors() {
        int step = field.getStep();

        if (step % 50 == 0) {
            spawnActor("gnome");
        }

        if (step % 200 == 0) {
            spawnActor("slime");
            spawnActor("elk");
            spawnActor("phoenix");
        }

        if (step % 500 == 0) {
            spawnActor("cactus");
            spawnActor("frost_flower");
        }
        
        if (step % 400 == 0) {
            spawnActor("diseased_animal");
        }

        if (step % 500 == 0) {
            spawnActor("wolf");
            spawnActor("tall_grass");
            spawnActor("dragon");
            spawnActor("diseased_animal");
            spawnActor("diseased_plant");
        }
    }

    /**
     * Simulate a single step of the simulation.
     */
    public void simulateStep() {
        // Update weather.
        weatherState.updateWeather();
        weatherController.updateTemperature();

        // Randomly insert Actors
        addActors();

        // Actors
        List<Actor> newActors = new ArrayList<>();
        for (Iterator<Actor> it = actors.iterator(); it.hasNext();) {
            Actor a = it.next();
            
            if (!a.isAlive()) {
                it.remove();
                continue;
            }

            a.act(newActors);
        }
        
        actors.addAll(newActors);
        field.incrementStep();
        fieldStatistics.update();
        //fieldStatistics.print();

        updateGUI();
    }

    /**
     * Simulate n steps with a delay of 10ms between each step.
     * @deprecated
     * This method should no longer be used since I've switched to a multithreaded
     * approach to support start/stop from the GUI.
     * 
     * @param n The number of steps to simulate.
     */
    @Deprecated
    public void simulateSteps(int n) {
        for (int i=0; i<n; i++) {
            simulateStep();
            delay(10);
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    /**
     * Reset the simulation (all of its states) and update the GUI.
     */
    public void reset() {
        weatherState.reset();
        field.reset();
        weatherController.reset();
        actors.clear();
        simulatorInitializer.populate();
        updateGUI();
    }
}
