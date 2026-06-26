import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


/**
 * A graphical view of the simulation   grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2022.03.3
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;



    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DISEASED_PREFIX = "Diseased: ";
    private JLabel stepLabel, population, infoLabel, diseasedPopulation;
    private FieldView fieldView;

    // adding the labels for weather and time
    private final String WEATHER_PREFIX = "Weather: ";
    private final String TIME_PREFIX = "Time: ";
    private JLabel weatherLabel, timeLabel;

    // adding the new buttons
    private JButton oneStepButton,stopButton, longButton, resetButton;
    // One population checkbox per species, built from the Species registry.
    private Map<Class<?>, JCheckBox> classToCheckBox;


    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;


    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Prey and predator simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        diseasedPopulation = new JLabel(DISEASED_PREFIX, JLabel.LEFT);


        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);


        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(3, 1));
        oneStepButton = new JButton("Simulate 1 step");
        stopButton = new JButton("Stop simulation");
        longButton = new JButton("Play 4000 steps");
        resetButton = new JButton("Reset simulation");
        toolbar.add(oneStepButton);
        toolbar.add(longButton);
        toolbar.add(stopButton);
        toolbar.add(resetButton);


        setLocation(0, 0);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(weatherLabel, BorderLayout.CENTER);
        infoPane.add(timeLabel, BorderLayout.EAST);

        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
//        contents.add(population, BorderLayout.SOUTH);
        contents.add(toolbar, BorderLayout.EAST);



        // Build a population checkbox per species, driven entirely by the Species
        // registry: label, colour, tooltip and toggle behaviour are all derived,
        // so adding a species needs no change here.
        classToCheckBox = new LinkedHashMap<>();
        JPanel checkboxes = new JPanel();
        checkboxes.setLayout(new BoxLayout(checkboxes, BoxLayout.Y_AXIS));
        checkboxes.add(new JLabel("POPULATIONS"));
        for(Species species : Species.values()) {
            Class<?> cls = species.actorClass();
            JCheckBox checkBox = new JCheckBox(species.displayName() + ": 0", true);
            checkBox.setForeground(species.color());
            checkBox.setToolTipText("Show/hide " + species.displayName() + " in the field view");
            checkBox.addItemListener(e -> checkCheckBoxByColor(cls));
            classToCheckBox.put(cls, checkBox);
            checkboxes.add(checkBox);
        }
        checkboxes.add(diseasedPopulation);

        toolbar.add(checkboxes);



        pack();
        setVisible(false);

        setColorsByCheckBox();


    }

    /**
     * Adding an actionListener for the oneStepButton
     */
    public void addOneStepButtonListener(ActionListener listener) {
        oneStepButton.addActionListener(listener);
    }

    /**
     * Adding an actionListener for the oneStepButton
     */
    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * Adding an actionListener for the stopButton
     */
    public void addStopButtonListener(ActionListener listener) {
        stopButton.addActionListener(listener);
    }

    /**
     * Adding an actionListener for the longButton
     */
    public void addLongButtonListener(ActionListener listener) {
        longButton.addActionListener(listener);
    }


    /**
     * Define a color to be used for a given class of animal.
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

    public void setColorsByCheckBox(){
        for(Species species : Species.values()){
            Class<?> c = species.actorClass();
            if(!classToCheckBox.get(c).isSelected()){
                setColor(c, Color.white);
            }
            else if(classToCheckBox.get(c).isSelected() && getColor(c).equals(UNKNOWN_COLOR)){
                setColor(c, species.color());
            }
        }
    }

    public void checkCheckBoxByColor(Class c){
        if(!classToCheckBox.get(c).isSelected()){
            setColor(c, Color.white);
        }
        else if(classToCheckBox.get(c).isSelected() && getColor(c).equals(Color.white)){
            setColor(c, Species.forClass(c).color());
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, String weather, String time, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        weatherLabel.setText(WEATHER_PREFIX + weather);
        timeLabel.setText(TIME_PREFIX + time);
        stats.reset();

        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getClass());
                    if(actor instanceof Organism){
                        if(((Organism) actor).isDiseased()){
                            stats.incrementDiseasedCount();
                        }
                    }
                    fieldView.drawMark(col, row, getColor(actor.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        for(Species species : Species.values()){
            classToCheckBox.get(species.actorClass()).setText(stats.getCountDetails(species.actorClass()));
        }

//        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        diseasedPopulation.setText(stats.getDiseasedPopulation());
        setColorsByCheckBox();
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
     * This is rather advanced GUI stuff - you can ignore This
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 4;

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
         * Paint on grid location on the field in a given color.
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
