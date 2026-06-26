import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
    
    private AreaChart<Number, Number> areaChart;
    private BarChart<String, Number> barChart;
    private BarChart<String, Number> plantBarChart;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private final HashMap<Class<?>, Counter> counters;
    private final TreeMap<Integer, Integer> diseaseStats;
    
    private final XYChart.Series<Number, Number> diseaseSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> populationSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> vegetationSeries = new XYChart.Series<>();   
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
        
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Population distribution");
        barChart.setLegendVisible(false);
        
        populationSeries.setName("Population");
        refreshSeries(populationSeries, AnimalType.values());
        barChart.getData().add(populationSeries);
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
        
        plantBarChart = new BarChart<>(plantXAxis, plantYAxis);
        plantBarChart.setTitle("Vegetation distribution");
        plantBarChart.setLegendVisible(false);
        
        vegetationSeries.setName("Vegetation");
        refreshSeries(vegetationSeries, PlantType.values());
        plantBarChart.getData().add(vegetationSeries);
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
        
        refreshDiseaseSeries();
        areaChart.getData().add(diseaseSeries);
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
     * Refresh the disease series from the accumulated infection map.
     */
    private void refreshDiseaseSeries()
    {
        diseaseSeries.getData().clear();
        for (Map.Entry<Integer, Integer> entry : diseaseStats.entrySet()) {
            diseaseSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Updates the population bar chart
     */
    private void updatePopulationChart()
    {
        Platform.runLater(() -> refreshSeries(populationSeries, AnimalType.values()));
    }
    
    /**
     * Updates the population bar chart.
     */
    private void updateVegetationChart()
    {
        Platform.runLater(() -> refreshSeries(vegetationSeries, PlantType.values()));
    }
    
    /**
     * Updates the disease time series.
     */
    private void updateDiseaseChart()
    {
        Platform.runLater(this::refreshDiseaseSeries);
    }

    /**
     * Populate a chart series in registry order.
     *
     * @param target the series to populate.
     * @param species the ordered species descriptors.
     */
    private void refreshSeries(XYChart.Series<String, Number> target, SpeciesDescriptor[] species)
    {
        target.getData().clear();
        for (SpeciesDescriptor descriptor : species) {
            Counter info = counters.get(descriptor.getActorClass());
            if(info != null) {
                target.getData().add(new XYChart.Data<>(descriptor.getDisplayName(), info.getCount()));
            }
        }
    }
}
