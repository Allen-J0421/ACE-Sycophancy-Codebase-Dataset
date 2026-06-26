import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.File;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.chart.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
/**
 * A dashboard showcasing the distribution of animals, plants as well as the evolution of the disease and the weather.
 *
 * @version 1.0
 */
public class Dashboard
{
    
    /*///////////////////////////////////////////////////////////////
                              UI COMPONENTS
    //////////////////////////////////////////////////////////////*/
    
    private JFrame frame;
    private AreaChart<Number, Number> areaChart;
    private BarChart barChart;
    private BarChart plantBarChart;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private final HashMap<Class<?>, Counter> counters;
    private TreeMap<Integer, Integer> diseaseStats;
    
    private XYChart.Series series = new XYChart.Series();
    private XYChart.Series data = new XYChart.Series();
    private XYChart.Series plantData = new XYChart.Series();    
    private NumberAxis diseaseXAxis;
    private NumberAxis diseaseYAxis;
    
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    
    private CategoryAxis plantXAxis;
    private NumberAxis plantYAxis;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a dashboard window.
     * 
     * @param counter Tracker for the population distribution of different animals
     * @param diseaseStats Tracker for the evolution of the disease
     */
    public Dashboard(HashMap<Class<?>, Counter> counters,TreeMap<Integer, Integer> diseaseStats)
    {
        // initialise instance variables
        //renderFrame();
        this.counters = counters;
        this.diseaseStats = diseaseStats;
        initAndShowGUI();
    }
    
    /**
     * Initialises and displays window.
     */
    public void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Simulation Dashboard");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(1100, 900);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }
    
    /**
     * Invoked method for the JavaFX thread.
     * 
     * @param fxPanel JavaFX component embedded in swing component.
     */
    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }
    
    /**
     * Creates the JavaFX scene.
     * 
     * @return the scene to be displayed in the swing window.
     */
    private Scene createScene() {
        Group  root  =  new  Group();
        Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10,10,10,10));
        pane.setMinSize(1100,900);
        
        //---BUILD DATA VISUALISATION --//
        
        buildAnimalPopulationChart();
        buildVegetationDistribution();
        buildDiseaseMonitor();
        
        //---APPEND CHARTS TO PANE---//
        
        pane.add(barChart,0,0);
        pane.add(areaChart,0,1);
        pane.add(plantBarChart,1,0);
        root.getChildren().add(pane);
        
        //---APPLY STYLING---//
        
        try{
           scene.getStylesheets().add("dashboard.css"); 
        } catch (Exception e) {
        }
        
        return (scene);
    }
    
    /*///////////////////////////////////////////////////////////////
                          CHART RENDERING LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a bar chart from the animal population distribution.
     */
    private void buildAnimalPopulationChart()
    {
        xAxis = new CategoryAxis();
        xAxis.setLabel("Animal");
        yAxis = new NumberAxis();
        yAxis.setLabel("Animal alive");
        
        barChart = new BarChart(xAxis, yAxis);
        barChart.setTitle("Population distribution");
        barChart.setLegendVisible(false);
        
        data = new XYChart.Series();
        data.setName("Population");
        
        for (Class<?> key : counters.keySet()) {
            if(Plant.class.isAssignableFrom(key)) {
                continue;
            }
            Counter info = counters.get(key);
            data.getData().add(new XYChart.Data(info.getName(), info.getCount()));
        }
        
        barChart.getData().add(data);
    }
    
    /**
     * Creates a bar chart from the plant data.
     */
    private void buildVegetationDistribution()
    {
        plantXAxis = new CategoryAxis();
        plantXAxis.setLabel("Vegetation");
        plantYAxis = new NumberAxis();
        plantYAxis.setLabel("Active Plants");
        
        plantBarChart = new BarChart(plantXAxis, plantYAxis);
        plantBarChart.setTitle("Vegetation distribution");
        plantBarChart.setLegendVisible(false);
        
        plantData = new XYChart.Series();
        plantData.setName("Vegetation");
        
        for (Class<?> key : counters.keySet()) {
            if(Animal.class.isAssignableFrom(key)) {
                continue;
            }
            Counter info = counters.get(key);
            plantData.getData().add(new XYChart.Data(info.getName(), info.getCount()));
        }
        
        plantBarChart.getData().add(plantData);
    }
    
    /**
     * Creates an area chart from the disease data.
     */
    private void buildDiseaseMonitor()
    {
        diseaseXAxis = new NumberAxis();
        diseaseYAxis = new NumberAxis();
        diseaseXAxis.setLabel("timestamp");
        diseaseYAxis.setLabel("number of infected");
        
        areaChart = new AreaChart<Number, Number>(diseaseXAxis, diseaseYAxis);
        areaChart.setTitle("Disease monitor");
        areaChart.setLegendVisible(false);
        
        for (int key: diseaseStats.keySet()) {
            series.getData().add(new XYChart.Data(key, diseaseStats.get(key)));
        }
       
        areaChart.getData().add(series);
    }
    
    /**
     * Updates the various charts/plots in the dashboard.
     */
    public void updateDashboard()
    {
        updateDiseaseChart();
        updatePopulationChart();
        updateVegetationChart();
    }
    
    /**
     * Updates the population bar chart
     */
    private void updatePopulationChart()
    {
        Platform.runLater(() -> {
            data.getData().clear();
            for (Class<?> key : counters.keySet()) {
                if(Plant.class.isAssignableFrom(key)) {
                    continue;
                }
                Counter info = counters.get(key);
                data.getData().add(new XYChart.Data(info.getName(), info.getCount()));
            }
        });
    }
    
    /**
     * Updates the population bar chart.
     */
    private void updateVegetationChart()
    {
        Platform.runLater(() -> {
            plantData.getData().clear();
            for (Class<?> key : counters.keySet()) {
                if(Animal.class.isAssignableFrom(key)) {
                    continue;
                }
                Counter info = counters.get(key);
                plantData.getData().add(new XYChart.Data(info.getName(), info.getCount()));
            }
        });
        
    }
    
    /**
     * Updates the disease time series.
     */
    private void updateDiseaseChart()
    {
        Platform.runLater(() -> {
            Map.Entry<Integer, Integer> entry = diseaseStats.lastEntry();
            series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));  
        });
    }
}
