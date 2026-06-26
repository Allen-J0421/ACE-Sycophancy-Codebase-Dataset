import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private static final String STEP_PREFIX       = "Step: ";
    private static final String POPULATION_PREFIX = "Population: ";
    private static final String WEATHER_PREFIX    = "Weather: ";
    private static final String DAYSTATE_PREFIX   = "Light: ";
    private JLabel stepLabel, population, infoLabel, dayLabel, weatherLabel;
    private JToolBar sideBar;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private Simulator simulator;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Create a view of the given width and height.
     * 
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator)
    {
        // set instance variables
        this.simulator = simulator;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        setTitle("Fox and Rabbit Simulation");
        
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("00 | 00", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        // setup dashboard button
        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setMargin(new Insets(4, 4, 4, 4));
        dashboardButton.addActionListener(e ->
            simulator.setDashboard(new Dashboard(stats.getCounters(), simulator.getDiseaseCount())));
        sideBar = new JToolBar(JToolBar.VERTICAL);
        
        dashboardButton.setAlignmentX(CENTER_ALIGNMENT);
        //setup sidebar + styling + eventhandler
        sideBar.setFloatable(false);
        sideBar.setMargin(new Insets(10, 10, 10, 10));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        //setup simulate step button + styling + evenhandler
        JButton simulateButton = new JButton("Simulate (1)");
        simulateButton.addActionListener(e -> simulator.simulateOneStep());
        simulateButton.setAlignmentX(CENTER_ALIGNMENT);
        simulateButton.setMargin(new Insets(4, 4, 4, 4));
        
        //setup simulate long simulation + eventhandler
        JButton simulateLong = new JButton("Simulate(1000)");
        simulateLong.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new Thread(simulator::runLongSimulation).start();
                }
        });
        simulateLong.setAlignmentX(CENTER_ALIGNMENT);
        simulateLong.setMargin(new Insets(4, 4, 4, 4));
        
        setLocation(100, 50);
        //Append components to toolbar
        sideBar.add(simulateButton);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(simulateLong);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(dashboardButton);
        // set up labels
        dayLabel = new JLabel("");
        weatherLabel= new JLabel("");
        dayLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        weatherLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        dayLabel.setBorder(new EmptyBorder(4,0,4,0));
        weatherLabel.setBorder(new EmptyBorder(4,0,4,0));
        sideBar.add(dayLabel);
        sideBar.add(weatherLabel);
        // create grid
        fieldView = new FieldView(height, width);
        
        Container contents = getContentPane();
        // append components to main frame
        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        contents.add(sideBar, BorderLayout.WEST);
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * 
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }
    
    /**
     * Displays colors associated with the animals in the side bar.
     */
    public void showColors()
    {
        for (Class cls : colors.keySet()) {
            JLabel tempLabel = new JLabel(cls.getName());
            tempLabel.setForeground(colors.get(cls));
            tempLabel.setAlignmentX(CENTER_ALIGNMENT);
            tempLabel.setBorder(new EmptyBorder(4,0,4,0));
            sideBar.add(tempLabel);
        }
        
    }
    
    /**
     * Returns a color to be used for a given class of animal
     * 
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * 
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field, SimulatorClock clock, Weather weather)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        infoLabel.setText(clock.getStringTime());
        weatherLabel.setText(WEATHER_PREFIX + weather.name().toLowerCase());
        dayLabel.setText(DAYSTATE_PREFIX + clock.getDayState().name().toLowerCase());
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                //animal takes precendence over plants
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    Plant plant = field.getPlantAt(row, col);
                    if(plant != null) {
                        stats.incrementCount(plant.getClass());
                        fieldView.drawMark(col, row, getColor(plant.getClass()));
                    } else {
                        fieldView.drawMark(col, row, EMPTY_COLOR);
                    }
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
