import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a coloured rectangle for each location
 * representing its contents. It uses a default background colour.
 * Colours for each type of species can be defined using the
 * setColor method.
 *
 * @version (28/02/2022)
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String DAY_PREFIX = "Day: ";
    private final String TIME_PREFIX = "Time: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, timeLabel, population, infoLabel, visibleLabel, controlLabel, playbackLabel, stepControlLabel, speedControlLabel, weatherLabel;
    private FieldView fieldView;

    // A map for storing the current colors for participants in the simulation.
    private Map<Class, Color> colors;
    // A map for storing colors for participants in the simulation.
    private Map<Class, Color> baseColors;
    // A statistics object computing and storing simulation information.
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        baseColors = new LinkedHashMap<>();

        setTitle("Predator and Prey Simulation");

        stepLabel = new JLabel(DAY_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        visibleLabel= new JLabel("Visibility Controls", JLabel.CENTER);
        controlLabel = new JLabel("Simulator Controls", JLabel.CENTER);
        playbackLabel = new JLabel("Playback Controls", JLabel.CENTER);
        stepControlLabel = new JLabel("Step Controls", JLabel.CENTER);
        speedControlLabel = new JLabel("Speed Controls", JLabel.CENTER);
        weatherLabel = new JLabel("Weather Controls", JLabel.CENTER);

        // Visibility toggle buttons - one per species, built from a single table.
        Object[][] speciesSpec = {
            {"Plant", Plant.class}, {"Dodo", Dodo.class}, {"Tortoise", Tortoise.class},
            {"Human", Human.class}, {"Monkey", Monkey.class}, {"Pig", Pig.class},
        };
        Map<Class, JButton> speciesButtons = new LinkedHashMap<>();
        for (Object[] spec : speciesSpec) {
            String label = (String) spec[0];
            Class actorClass = (Class) spec[1];
            speciesButtons.put(actorClass, button(label, e -> toggleColor(actorClass, simulator.getField())));
        }

        JButton resetClearButton = button("Reset Colours", e -> resetViewColor(simulator.getField()));

        // Weather buttons - "Random Weather" clears the override (null); the rest pick a Weather value.
        JButton randomWeatherButton = button("Random Weather", e -> simulator.setCurrentWeather(null));
        Object[][] weatherSpec = {
            {"Sunny Weather", Weather.SUNNY}, {"Rainy Weather", Weather.RAINY},
            {"Foggy Weather", Weather.FOGGY}, {"Snowy Weather", Weather.SNOWY},
        };
        Map<Weather, JButton> weatherButtons = new EnumMap<>(Weather.class);
        for (Object[] spec : weatherSpec) {
            Weather weather = (Weather) spec[1];
            weatherButtons.put(weather, button((String) spec[0], e -> simulator.setCurrentWeather(weather)));
        }

        // Simulation control buttons - each drives a distinct simulator action.
        JButton shutSimulationButton = button("Shutdown Simulatior", e -> simulator.shutdownSimulation());
        JButton oneStepButton = button("Simulate One Step", e -> simulator.forceSimulateOneStep());
        JButton resetSimulationButton = button("Reset Field", e -> simulator.reset());
        JButton longSimulationButton = button("Long Simulation", e -> simulator.runLongSimulation());
        JButton shortSimulationButton = button("Short Simulation", e -> simulator.runShortSimulation());
        JButton pauseSimulationButton = button("Pause Simulation", e -> simulator.setPauseSimulation(true));
        JButton playSimulationButton = button("Play Simulation", e -> simulator.setPauseSimulation(false));
        JButton speedUpButton = button("Speed Up Simulation", e -> simulator.speedUpTimeDelay());
        JButton slowDownButton = button("Slow Down Simulation", e -> simulator.slowDownTimeDelay());

        setLocation(100, 50);
        fieldView = new FieldView(height, width);
        Container contents = getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(timeLabel, BorderLayout.EAST);
        infoPane.add(infoLabel, BorderLayout.CENTER);

        JPanel visionButtonPane = new JPanel(new BorderLayout());
        JPanel northVisionButtonPane = new JPanel(new BorderLayout());
        visionButtonPane.add(northVisionButtonPane, BorderLayout.NORTH);

        JPanel northVisionButtonPane1 = new JPanel(new BorderLayout());
        JPanel northVisionButtonPane2 = new JPanel(new BorderLayout());
        JPanel northVisionButtonPane3 = new JPanel(new BorderLayout());

        northVisionButtonPane.add(northVisionButtonPane1, BorderLayout.NORTH);
        northVisionButtonPane.add(northVisionButtonPane2, BorderLayout.CENTER);
        northVisionButtonPane.add(northVisionButtonPane3, BorderLayout.SOUTH);

        northVisionButtonPane1.add(visibleLabel, BorderLayout.NORTH);
        northVisionButtonPane1.add(speciesButtons.get(Plant.class), BorderLayout.CENTER);
        northVisionButtonPane1.add(speciesButtons.get(Dodo.class), BorderLayout.SOUTH);

        northVisionButtonPane2.add(speciesButtons.get(Tortoise.class), BorderLayout.NORTH);
        northVisionButtonPane2.add(speciesButtons.get(Human.class), BorderLayout.CENTER);
        northVisionButtonPane2.add(speciesButtons.get(Monkey.class), BorderLayout.SOUTH);

        northVisionButtonPane3.add(speciesButtons.get(Pig.class), BorderLayout.NORTH);
        northVisionButtonPane3.add(resetClearButton, BorderLayout.CENTER);

        JPanel weatherButtonPane = new JPanel(new BorderLayout());
        visionButtonPane.add(weatherButtonPane, BorderLayout.SOUTH);

        JPanel weatherButtonPane1 = new JPanel(new BorderLayout());
        JPanel weatherButtonPane2 = new JPanel(new BorderLayout());

        weatherButtonPane.add(weatherButtonPane1, BorderLayout.NORTH);
        weatherButtonPane.add(weatherButtonPane2, BorderLayout.CENTER);

        weatherButtonPane1.add(weatherLabel, BorderLayout.NORTH);
        weatherButtonPane1.add(randomWeatherButton, BorderLayout.CENTER);
        weatherButtonPane1.add(weatherButtons.get(Weather.SUNNY), BorderLayout.SOUTH);

        weatherButtonPane2.add(weatherButtons.get(Weather.RAINY), BorderLayout.NORTH);
        weatherButtonPane2.add(weatherButtons.get(Weather.FOGGY), BorderLayout.CENTER);
        weatherButtonPane2.add(weatherButtons.get(Weather.SNOWY), BorderLayout.SOUTH);

        JPanel controlButtonPane = new JPanel(new BorderLayout());
        JPanel northControlButtonPane = new JPanel(new BorderLayout());
        controlButtonPane.add(northControlButtonPane, BorderLayout.NORTH);

        JPanel northControlButtonPane1 = new JPanel(new BorderLayout());
        JPanel northControlButtonPane2 = new JPanel(new BorderLayout());
        JPanel northControlButtonPane3 = new JPanel(new BorderLayout());
        northControlButtonPane.add(northControlButtonPane1, BorderLayout.NORTH);
        northControlButtonPane.add(northControlButtonPane2, BorderLayout.CENTER);
        northControlButtonPane.add(northControlButtonPane3, BorderLayout.SOUTH);

        northControlButtonPane1.add(playbackLabel, BorderLayout.NORTH);
        northControlButtonPane1.add(playSimulationButton, BorderLayout.CENTER);
        northControlButtonPane1.add(pauseSimulationButton, BorderLayout.SOUTH);

        northControlButtonPane2.add(speedControlLabel, BorderLayout.NORTH);
        northControlButtonPane2.add(speedUpButton, BorderLayout.CENTER);
        northControlButtonPane2.add(slowDownButton, BorderLayout.SOUTH);

        JPanel centerControlButtonPane = new JPanel(new BorderLayout());
        controlButtonPane.add(centerControlButtonPane, BorderLayout.CENTER);
        JPanel nCenterControlButtonPane = new JPanel(new BorderLayout());
        centerControlButtonPane.add(nCenterControlButtonPane, BorderLayout.NORTH);

        JPanel nCenterControlButtonPane1 = new JPanel(new BorderLayout());
        JPanel nCenterControlButtonPane2 = new JPanel(new BorderLayout());
        JPanel nCenterControlButtonPane3 = new JPanel(new BorderLayout());
        nCenterControlButtonPane.add(nCenterControlButtonPane1, BorderLayout.NORTH);
        nCenterControlButtonPane.add(nCenterControlButtonPane2, BorderLayout.CENTER);
        nCenterControlButtonPane.add(nCenterControlButtonPane3, BorderLayout.SOUTH);

        nCenterControlButtonPane1.add(stepControlLabel, BorderLayout.NORTH);

        nCenterControlButtonPane2.add(oneStepButton, BorderLayout.NORTH);
        nCenterControlButtonPane2.add(shortSimulationButton, BorderLayout.CENTER);
        nCenterControlButtonPane2.add(longSimulationButton, BorderLayout.SOUTH);        

        JPanel southControlButtonPane = new JPanel(new BorderLayout());
        controlButtonPane.add(southControlButtonPane, BorderLayout.SOUTH);

        JPanel southControlButtonPane1 = new JPanel(new BorderLayout());
        southControlButtonPane.add(southControlButtonPane1, BorderLayout.NORTH);

        southControlButtonPane1.add(controlLabel, BorderLayout.NORTH);
        southControlButtonPane1.add(resetSimulationButton, BorderLayout.CENTER);
        southControlButtonPane1.add(shutSimulationButton, BorderLayout.SOUTH);


        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        contents.add(visionButtonPane, BorderLayout.EAST);
        contents.add(controlButtonPane, BorderLayout.WEST);
        pack();
        setVisible(true);
    }

    /**
     * Create a button with the given label and action. Centralises the
     * button-plus-listener construction so the UI setup no longer needs a
     * repeated anonymous ActionListener for every button.
     *
     * @param text   The button label.
     * @param action The action to run when the button is pressed.
     * @return The configured button.
     */
    private JButton button(String text, ActionListener action)
    {
        JButton b = new JButton(text);
        b.addActionListener(action);
        return b;
    }

    /**
     * Define a color to be used for a given class of actor.
     *
     * @param actorClass The actor's Class object.
     * @param color      The color to be used for the given class.
     */
    public void setColor(Class actorClass, Color color)
    {
        colors.put(actorClass, color);
        baseColors.put(actorClass, color);
    }

    /**
     * Toggles the colour of the actor between clear and coloured
     * 
     * @param actorClass The actor's Class object, 
     * @param field The field in the simulator view.
     */
    private void toggleColor(Class actorClass, Field field)
    {
        if(EMPTY_COLOR.equals(colors.get(actorClass))){
            colors.replace(actorClass, baseColors.get(actorClass));
        }
        else{
            colors.replace(actorClass, EMPTY_COLOR);
        }

        updatePanel(field, false);
    }

    /**
     * Bring all colours into view.
     * 
     * @param field The field in the simulator view.
     */
    private void resetViewColor(Field field){
        baseColors.forEach((key,entry) -> colors.replace(key,entry)); 
        updatePanel(field, false);
    }

    /**
     * Display a short information label at the top of the window.
     * 
     * @param text the text to be displayed.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Returns the colour that will be used for a given class.
     * 
     * @param actorClass An actor class.
     * @return The color to be used for a given class of actor.
     */
    private Color getColor(Class actorClass)
    {
        Color col = colors.get(actorClass);
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
     * @param totalSteps The number of steps the simulator is running for.
     * @param field The field whose status is to be displayed.
     * @param weather The current weather of the board.
     * @param virusCount The number of currently infected animals.
     */
    public void showStatus(int step, int totalSteps, Field field, Weather weather, int virusCount)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        // Displays day number.
        stepLabel.setText(DAY_PREFIX + (step+1)/2 + "/" + totalSteps/2);
        // Computes and displays time of day.
        String time = "";
        if(step%2 == 0){
            time = "Night";
        }
        else if(step%2 == 1){
            time = "Day";
        }
        timeLabel.setText(TIME_PREFIX + time);

        // Displays additional info such as weather and virus numbers.
        setInfoText("Weather:" + weather + "   Infected :" + virusCount);
        stats.reset();

        updatePanel(field, true);
    }

    /**
     * Clears and repaints the field on the viewer.
     * 
     * @param field The field the viewer is currently representing.
     * @param newStep Defines whether a new step has occured or not.
     */
    private void updatePanel(Field field, boolean newStep)
    {
        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Actor actor = (Actor) field.getObjectAt(row, col);
                if (actor != null) {
                    // Increments population
                    if(newStep){
                        stats.incrementCount(actor.getClass()); 
                    }
                    fieldView.drawMark(col, row, getColor(actor.getClass()));
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determines whether the simulation should continue to run.
     * 
     * @param field The field the viewer is displaying.
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
         * 
         * @param height The height of the field.
         * @param width The width of the field.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         * 
         * @return The field size.
         */
        @Override
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
         * 
         * @param x The x-coordinate in the field.
         * @param y The y-coordinate in the field.
         * @param color The color to be painted on the field.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         * 
         * @param g An object of the Graphics class.
         */
        @Override
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
