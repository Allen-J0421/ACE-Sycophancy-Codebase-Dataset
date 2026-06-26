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

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final String DAYSTATE_PREFIX = "Light: ";
    private JLabel stepLabel, population, infoLabel, dayLabel, weatherLabel;
    private final JToolBar sideBar;
    private final FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private final Map<Class<?>, Color> colors;
    // A map for storing the user-facing labels for participants in the simulation
    private final Map<Class<?>, String> labels;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final Simulator simulator;
    
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
        this.simulator = simulator;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        labels = new LinkedHashMap<>();
        setTitle("Fox and Rabbit Simulation");
        
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("00 | 00", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        sideBar = buildSideBar();
        setLocation(100, 50);
        fieldView = new FieldView(height, width);
        configureLayout();
        pack();
        setVisible(true);
    }

    /**
     * Build the vertical sidebar and its controls.
     *
     * @return the configured sidebar.
     */
    private JToolBar buildSideBar()
    {
        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(10, 10, 10, 10));
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
        toolBar.add(createStepButton());
        toolBar.add(Box.createVerticalStrut(8));
        toolBar.add(createLongSimulationButton());
        toolBar.add(Box.createVerticalStrut(8));
        toolBar.add(createDashboardButton());
        toolBar.add(Box.createVerticalStrut(8));
        toolBar.add(createStateLabel(dayLabel = new JLabel("")));
        toolBar.add(createStateLabel(weatherLabel = new JLabel("")));
        return toolBar;
    }

    /**
     * Configure the frame layout.
     */
    private void configureLayout()
    {
        Container contents = getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        contents.add(sideBar, BorderLayout.WEST);
    }

    /**
     * Create a toolbar button with shared margin and alignment settings.
     *
     * @param text the button label.
     * @return a configured button.
     */
    private JButton createToolbarButton(String text)
    {
        JButton button = new JButton(text);
        button.setMargin(new Insets(4, 4, 4, 4));
        button.setAlignmentX(CENTER_ALIGNMENT);
        return button;
    }

    /**
     * Create the button used to run one simulation step.
     *
     * @return the configured step button.
     */
    private JButton createStepButton()
    {
        JButton button = createToolbarButton("Simulate (1)");
        button.addActionListener(e -> simulator.simulateOneStep());
        return button;
    }

    /**
     * Create the button used to launch the long simulation.
     *
     * @return the configured long-run button.
     */
    private JButton createLongSimulationButton()
    {
        JButton button = createToolbarButton("Simulate(1000)");
        button.addActionListener(e -> new Thread(simulator::runLongSimulation).start());
        return button;
    }

    /**
     * Create the button used to open the dashboard.
     *
     * @return the configured dashboard button.
     */
    private JButton createDashboardButton()
    {
        JButton button = createToolbarButton("Dashboard");
        button.addActionListener(e -> simulator.setDashboard(new Dashboard(stats.getCounters(), DiseaseHandler.count)));
        return button;
    }

    /**
     * Create a vertically aligned state label for the sidebar.
     *
     * @param label the label to format.
     * @return the configured label.
     */
    private JLabel createStateLabel(JLabel label)
    {
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(4,0,4,0));
        return label;
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * 
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
        labels.putIfAbsent(animalClass, animalClass.getSimpleName());
    }

    /**
     * Register a species with its display label and color.
     *
     * @param species the species to register.
     */
    public void registerSpecies(SpeciesDescriptor species)
    {
        colors.put(species.getActorClass(), species.getColor());
        labels.put(species.getActorClass(), species.getDisplayName());
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
        for (Class<?> cls : colors.keySet()) {
            JLabel tempLabel = new JLabel(labels.getOrDefault(cls, cls.getSimpleName()));
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
    private Color getColor(Class<?> animalClass)
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
                Animal animal = field.getAnimalAt(row, col);
                //animal takes precendence over plants
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else if(field.getPlantAt(row, col) != null) {
                    // if the cell is not occupied by an animal, attempt to occupy with plant. 
                    Plant plant = field.getPlantAt(row, col);
                    stats.incrementCount(plant.getClass());
                    fieldView.drawMark(col, row, getColor(plant.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
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
