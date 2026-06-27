import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.event.ActionListener;
import javax.swing.border.LineBorder;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2022.02.28
 */
public class SimulatorView extends JFrame
{
    // Color used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    // Prefix preceding the step count.
    private static final String STEP_PREFIX = "Step: ";
    // Prefix preceding the current time display.
    private static final String TIME_PREFIX = "Time: ";
    // Prefix preceding the current temperature display.
    private static final String TEMPERATURE_PREFIX = "Temperature: ";
    // Prefix preceding the current season display.
    private static final String SEASON_PREFIX = "Season: ";
    // JLabels for all the previously mentioned information displays.
    private JLabel stepLabel, infoLabel, timeLabel, temperatureLabel, seasonLabel;
    // Container to show population data.
    private JPanel population;
    // The grid view showing the state of the field at each step.
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation
    private Map<String, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    // The GUIHandler governing the simulation's GUI.
    private GUIHandler handler;

    /**
     * Constructor has two roles:
     *      1) Initializing the fields.
     *      2) Building the view.
     *
     * @param height (int) The simulation's height.
     * @param width (int) The simulation's width.
     */
    public SimulatorView(int height, int width, GUIHandler handler)
    {
        // INITIALIZES FIELDS
        this.handler = handler;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        // Various information labels initialization.
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        temperatureLabel = new JLabel(TEMPERATURE_PREFIX, JLabel.CENTER);
        seasonLabel = new JLabel(SEASON_PREFIX, JLabel.CENTER);

        // Population display initialization.
        population = new JPanel();
        GridLayout gridLayout = new GridLayout(5,5);
        population.setLayout(gridLayout);

        fieldView = new FieldView(height, width);

        // BUILDS THE VIEW
        setTitle("Ultimate Simulator 3000");
        setMinimumSize(new Dimension(800, 600));

        // Information display on habitat conditions and step counter.
        FlowLayout simInfo = new FlowLayout();
        simInfo.setHgap(50);

        JPanel infoPane = new JPanel(simInfo);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(stepLabel, BorderLayout.CENTER);
        infoPane.add(timeLabel, BorderLayout.CENTER);
        infoPane.add(seasonLabel, BorderLayout.CENTER);
        infoPane.add(temperatureLabel, BorderLayout.CENTER);

        // Various buttons for user to interact with the simulation.
        JButton launchLongSimButton = new JButton("Launch long simulation");
        ActionListener launchLongSim = e -> {
            handler.launchLongSimulation();
        };
        launchLongSimButton.addActionListener(launchLongSim);
        
        JButton launchHundredStepsButton = new JButton("Run 100 steps");
        ActionListener launchHundredSteps = e -> {
            handler.runHundredSteps();
        };
        launchHundredStepsButton.addActionListener(launchHundredSteps);
        
        JButton launchOneStepButton  = new JButton("Run 1 step");
        ActionListener launchOneStep = e -> {
            handler.runOneStep();
        };
        launchOneStepButton.addActionListener(launchOneStep);
        
        JButton goBackMenuButton  = new JButton("Run a new simulation");
        ActionListener goBackMenu = e -> {
            setVisible(false);
            handler.switchToMenuView();
        };
        goBackMenuButton.addActionListener(goBackMenu);

        // Bringing the UI together.
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(launchLongSimButton);
        buttons.add(launchHundredStepsButton);
        buttons.add(launchOneStepButton);
        buttons.add(goBackMenuButton);
        
        Box bottomComponents = Box.createVerticalBox();
        bottomComponents.add(population);
        bottomComponents.add(buttons);
        
        JPanel centeredFieldView = new JPanel(new BorderLayout());
        centeredFieldView.add(fieldView, BorderLayout.CENTER);
        add(infoPane, BorderLayout.NORTH);
        add(fieldView, BorderLayout.CENTER);
        add(bottomComponents, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    /**
     * Define a color to be used for a given class of specie.
     *
     * @param speciesName (String) The specie's name.
     * @param color (Color) The color to be used for the given class.
     */
    public void setColor(String speciesName, Color color)
    {
        colors.put(speciesName, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return (Color) The color to be used for a given class of specie.
     */
    private Color getColor(String speciesName)
    {
        Color col = colors.get(speciesName);
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
     * @param step (int) Which iteration step it is.
     * @param time (String) The current time in the simulation
     * @param season (String) The current season in the simulation
     * @param temperature (int) The current temperature in the simulation
     * @param field (Field) The field whose status is to be displayed.
     */
    public void showStatus(int step, String time, String season, int temperature, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        timeLabel.setText(TIME_PREFIX + time);
        seasonLabel.setText(SEASON_PREFIX + season);
        temperatureLabel.setText(TEMPERATURE_PREFIX + temperature + " C");
        stats.reset();

        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++)
        {
            for(int col = 0; col < field.getWidth(); col++)
            {
                Species specie = field.getObjectAt(row, col);
                if(specie != null) {
                    stats.incrementCount(specie.getName());
                    fieldView.drawMark(col, row, getColor(specie.getName()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        
        fieldView.repaint();
        generatePopulationComponent(field);
    }
    
    /**
     * Generates a population component with the color, count, and name of each species present in the simulation.
     * The technique to generate small square of a given color was found on https://zetcode.com/javaswing/basicswingcomponentsII/
     *
     * @param field (Field) The field whose population status has to be displayed.
     */
    private void generatePopulationComponent(Field field)
    {
        population.removeAll();
        stats.checkCountIsValid(field);
        for (String speciesName : colors.keySet()) {
            int count = stats.getCount(speciesName);
            
            Box speciesDetails = Box.createHorizontalBox();
            JPanel colorDisplay = new JPanel();
            colorDisplay.setMaximumSize(new Dimension(10, 10));
            colorDisplay.setBorder(LineBorder.createGrayLineBorder());
            colorDisplay.setBackground(colors.get(speciesName));
            JLabel nameAndCount = new JLabel(" " + speciesName + ": " + count);
            speciesDetails.add(colorDisplay);
            speciesDetails.add(nameAndCount);
            population.add(speciesDetails);
        }
    }

    /**
     * Determine whether the simulation should continue to run.
     *
     * @return (boolean) true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is a nested class (a class defined inside a class) which defines a
     * custom component for the user interface. This component displays the field.
     * This is rather advanced GUI stuff - you can ignore this for your project if you like.
     */
    class FieldView extends JPanel
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
         * Prepare for a new round of painting. Since the component may be resized, compute the scaling factor again.
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
         * The field view component needs to be redisplayed. Copy the internal image to screen.
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