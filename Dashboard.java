import javax.swing.JFrame;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import java.util.Map;
import java.util.NavigableMap;
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
    private BarChart<String, Number> barChart;
    private BarChart<String, Number> plantBarChart;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private Map<ActorType, Counter> counters;
    private NavigableMap<Integer, Integer> diseaseStats;
    
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private XYChart.Series<String, Number> data = new XYChart.Series<>();
    private XYChart.Series<String, Number> plantData = new XYChart.Series<>();    
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
     * @param counters Tracker for the population distribution of different actors
     * @param diseaseStats Tracker for the evolution of the disease
     */
    public Dashboard(Map<ActorType, Counter> counters, NavigableMap<Integer, Integer> diseaseStats)
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
        frame = new JFrame("Simulation Dashboard");
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
        yAxis = new NumberAxis();
        data = new XYChart.Series<>();
        data.setName("Population");

        barChart = createDistributionChart(
            xAxis,
            yAxis,
            "Animal",
            "Animal alive",
            "Population distribution",
            data,
            ActorCategory.ANIMAL
        );
    }
    
    /**
     * Creates a bar chart from the plant data.
     */
    private void buildVegetationDistribution()
    {
        plantXAxis = new CategoryAxis();
        plantYAxis = new NumberAxis();
        plantData = new XYChart.Series<>();
        plantData.setName("Vegetation");

        plantBarChart = createDistributionChart(
            plantXAxis,
            plantYAxis,
            "Vegetation",
            "Active Plants",
            "Vegetation distribution",
            plantData,
            ActorCategory.PLANT
        );
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
            series.getData().add(new XYChart.Data<>(key, diseaseStats.get(key)));
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
        updateDistributionSeries(data, ActorCategory.ANIMAL);
    }
    
    /**
     * Updates the population bar chart.
     */
    private void updateVegetationChart()
    {
        updateDistributionSeries(plantData, ActorCategory.PLANT);
    }
    
    /**
     * Updates the disease time series.
     */
    private void updateDiseaseChart()
    {
        Platform.runLater(() -> {
            Map.Entry<Integer, Integer> entry = diseaseStats.lastEntry();
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));  
        });
    }

    private BarChart<String, Number> createDistributionChart(
        CategoryAxis categoryAxis,
        NumberAxis numberAxis,
        String categoryLabel,
        String valueLabel,
        String title,
        XYChart.Series<String, Number> chartData,
        ActorCategory actorCategory
    ) {
        categoryAxis.setLabel(categoryLabel);
        numberAxis.setLabel(valueLabel);

        BarChart<String, Number> chart = new BarChart<>(categoryAxis, numberAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        populateDistributionSeries(chartData, actorCategory);
        chart.getData().add(chartData);
        return chart;
    }

    private void updateDistributionSeries(XYChart.Series<String, Number> chartData, ActorCategory actorCategory)
    {
        Platform.runLater(() -> {
            chartData.getData().clear();
            populateDistributionSeries(chartData, actorCategory);
        });
    }

    private void populateDistributionSeries(XYChart.Series<String, Number> chartData, ActorCategory actorCategory)
    {
        for (Map.Entry<ActorType, Counter> entry : counters.entrySet()) {
            if(entry.getKey().getCategory() != actorCategory) {
                continue;
            }
            Counter info = entry.getValue();
            chartData.getData().add(new XYChart.Data<>(info.getName(), info.getCount()));
        }
    }
}
