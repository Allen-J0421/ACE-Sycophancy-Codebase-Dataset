import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * A graphical view of the population of each species over time as the
 * simulation runs its courses.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class StatisticsView extends Application
{
    // Constants representing the JavaFX window properties:
    //   The default size of the window:
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    //   The default title of the window:
    private static final String WINDOW_TITLE = "Statistics";
    
    // Constants representing JavaFX GUI components:
    //   The root layout component:
    private static BorderPane root;
    //   An informational label explaining the window:
    private static Label infoLabel;
    //   The line chart showing populations:
    private static LineChart<Number, Number> lineChart;
    //   The axes of the population line chart:
    private static NumberAxis xAxis, yAxis;
    //   The XYSeries representing each population over time:
    private static Map<Class<?>, Series<Number, Number>> actorXYSeries = new HashMap<>();
    
    // The time between updates for the stats:
    private static final double UPDATE_TIME_IN_SECONDS = 0.1;
    // The last day recorded:
    private static int lastDayRecorded = -1; // Set to -1 so we record day 0
    // The highest recorded population:
    private static int highestPopulation = 0;
    
    /**
     * @param stage The stage (window) for JavaFX.
     */
    @Override
    public void start(Stage stage)
    {
        // Create the root layout component:
        root = new BorderPane();
        
        // Create the scene:
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Create the informational label:
        infoLabel = new Label("The chart below shows the population of each species over time:");
        
        // Create the line chart:
        lineChart = getPopulationLineChart();
        lineChart.setAnimated(false);
        
        // Set the initial properties of the root BorderPane:
        final int ROOT_PADDING = 5;
        root.setTop(infoLabel);
        root.setCenter(lineChart);
        root.setPadding(new Insets(ROOT_PADDING, ROOT_PADDING, ROOT_PADDING, ROOT_PADDING));
        
        // Set the initial properties of the stage and show it:
        stage.setScene(scene);
        stage.setTitle(WINDOW_TITLE);
        stage.show();
        
        // Create a timeline to update the population line chart at a fixed rate:
        KeyFrame keyFrame = new KeyFrame(Duration.millis(UPDATE_TIME_IN_SECONDS * 1000), e -> updatePopulationLineChart());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Create a line chart for the population.
     * 
     * @return A line chart representing population.
     */
    private static LineChart<Number, Number> getPopulationLineChart()
    {
        // Create the axes:
        final int xAxisStep = 5;
        final int yAxisStep = 1000;
        xAxis = new NumberAxis(0, 0, xAxisStep);
        yAxis = new NumberAxis(0, 0, yAxisStep);
        xAxis.setLabel("Days");
        yAxis.setLabel("Population");
        
        // Create the line chart itself:
        return new LineChart<>(xAxis, yAxis);
    }
    
    /**
     * Update the data for the population line chart. This is called
     * periodically.
     */
    private void updatePopulationLineChart()
    {
        // Reset if necessary:
        if (Simulator.resetStatisticsView)
        {
            resetPopulationLineChart();
            Simulator.resetStatisticsView = false;
        }
        
        // Guard statement: Don't continue with this if the day
        // hasn't changed since the last update:
        if (lastDayRecorded == TimeSystem.getCurrentDay()) return;
        
        // Iterate through each counter in FieldStats:
        Field field = Simulator.getCurrentField();
        Map<Class<?>, Counter> counters = FieldStats.getCounters(field);
        
        for (Class<?> key : counters.keySet())
        {
            Counter counter = counters.get(key);
            
            // Get the new data to add to the XYSeries:
            Data<Number, Number> newData = new Data<>(TimeSystem.getCurrentDay(), counter.getCount());
            
            // Create a new XYSeries for this actor if there is none already:
            if (!actorXYSeries.containsKey(key))
            {
                Series<Number, Number> series = new Series<>();
                series.setName(counter.getName());
                
                lineChart.getData().add(series);
                
                actorXYSeries.put(key, series);
            }
            
            // Update the XYSeries for this Actor:
            Series<Number, Number> series = actorXYSeries.get(key);
            series.getData().add(newData); 
            
            // Update the highest population if necessary:
            if (counter.getCount() > highestPopulation) highestPopulation = counter.getCount();
        }
        
        // Set the new last day recorded:
        lastDayRecorded = TimeSystem.getCurrentDay();
        
        // Set the upper bounds for each of the axes:
        xAxis.setUpperBound(TimeSystem.getCurrentDay());
        yAxis.setUpperBound(highestPopulation);
    }
    
    /**
     * Reset the population line chart.
     */
    public static void resetPopulationLineChart()
    {
        lineChart = getPopulationLineChart();
        root.setCenter(lineChart);
        
        lineChart.getData().clear();
        
        actorXYSeries.clear();
        
        lastDayRecorded = -1;
        
        highestPopulation = 0;
    }
}
