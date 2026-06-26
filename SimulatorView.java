import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 26/02/2022
 */
@SuppressWarnings({"serial", "this-escape"})
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "  Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String TIME_PREFIX = "Time: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final String INFECTED_PREFIX = "Infected: ";
    private final String IMMUNE_PREFIX = "Immune: ";
    
    private JLabel stepLabel, population, infoLabel, timeLabel, 
                   weatherLabel, infectedLabel, immuneLabel;
    
    // The view of the simulation.
    private FieldView fieldView;
    
    // Stores a graph key which displays the colors of the classes.
    private JPanel classKey;
    
    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
    
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width The simulation's width.
     * @param buttons The buttons used to control the simulation.
     */
    public SimulatorView(int height, int width, JButton[] buttons)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Savannah Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        infectedLabel = new JLabel(INFECTED_PREFIX, JLabel.CENTER);
        immuneLabel = new JLabel(IMMUNE_PREFIX, JLabel.CENTER);
        
        fieldView = new FieldView(height, width);

        // Initialise all the JFrame elements        
        Container contents = getContentPane();
        
        // The menu bar at the top of the screen
        JMenuBar optionsBar = makeMenuBar();
            setJMenuBar(optionsBar);
        
        // The labels above the simulator
        JPanel infoPanel = new JPanel(new GridLayout());
            infoPanel.add(stepLabel);
            infoPanel.add(timeLabel);
            infoPanel.add(weatherLabel);
        
        classKey = new JPanel(new GridLayout());
        
        // The labels below the simulator
        JPanel populationPanel = new JPanel(new BorderLayout());
            populationPanel.add(classKey, BorderLayout.NORTH);
            populationPanel.add(infectedLabel, BorderLayout.WEST);
            populationPanel.add(population, BorderLayout.CENTER);
            populationPanel.add(immuneLabel, BorderLayout.EAST);
            
        // Adds the buttons to the side menu panel.
        JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                menuPanel.add(buttons[i]);
            }
        
        // add everything to the windows
        contents.add(infoPanel, BorderLayout.NORTH);
        contents.add(menuPanel, BorderLayout.WEST);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(populationPanel, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    /**
     * Creates a menu bar at the top of the screen which different 
     * options.
     * @return Returns the new menu bar.
     */
    public JMenuBar makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menu;
        JMenuItem menuItem;
        
        menu = new JMenu("Help");
            menuBar.add(menu);
        
            menuItem = new JMenuItem("Instructions");
                menuItem.addActionListener(e -> showInstructions());
                menu.add(menuItem);
                
            menuItem = new JMenuItem("About");
                menuItem.addActionListener(e -> showAbout());
                menu.add(menuItem);
        
        return menuBar;
    }
    
    /**
     * Displays a new popup screen containing instructions on how
     * to use the simulation.
     */
    public void showInstructions() 
    {
        String dialogue = "How to use:\n" +
                          "   - Use 'Play/Pause' button to stop and resume the simulation whilst in progress.\n" +
                          "   - Use the 'Run Long Sim' button to show the 500 steps of the simulation.\n" +
                          "   - Use the 'Sim One Step' button to show the next step of the simulation.\n" +
                          "   - Use the 'Reset' button whilst paused/finished to generate a brand new random seed.\n" +
                          "   - Use the 'Quit' button to exit the application.";
        JOptionPane.showMessageDialog (this, dialogue, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays a new popup screen containing information about the
     * project.
     */
    private void showAbout()
    {
        String dialogue = "Project: Savannah Simulation which is predator-prey-simulation\n" +
                          "Authors: \n" +
                          "             based on foxes-and-rabbits by Michael KÃ¶lling and David J. Barnes.\n\n" + 
                          "This project is based on part of the material for chapter 10 of the book\n" + 
                          "         Objects First with Java - A Practical Introduction using BlueJ\n" +
                          "         Sixth edition\n" +
                          "         David J. Barnes and Michael KÃ¶lling\n" +
                          "         Pearson Education, 2016\n\n" +
                          "A predator-prey simulation involving 2 predators (Cheetahs and Lions),\n" + 
                          "3 prey (Lemurs, Giraffes, and Zebras), and grass in an enclosed rectangular\n" + 
                          "field.";
        JOptionPane.showMessageDialog (this, dialogue, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Define a color to be used for the given class.
     *
     * @param newClass The new Class' object.
     * @param classColor The color to be used for the given class.
     * @param textColor The color to be used for the text in the class key.
     */
    public void setColor(Class<?> newClass, Color classColor, Color textColor)
    {
        colors.put(newClass, classColor);
        
        // Creates label of the appropriate color and text to be
        // displayed by the class key.
        JLabel newClassLabel = new JLabel(newClass.getName(), JLabel.CENTER);
        
        // Adjusts color of the label's text
        newClassLabel.setForeground(textColor);
        
        newClassLabel.setBackground(classColor);
        newClassLabel.setOpaque(true);
        
        classKey.add(newClassLabel);
    }

    /**
     * @param animalClass The class to get the colour of.
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
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + Time.getStep());
        timeLabel.setText(TIME_PREFIX + getTimeString());
        weatherLabel.setText(WEATHER_PREFIX + Weather.getWeather() + "  ");
        stats.reset();
        
        fieldView.preparePaint();
        
        // Used to tally up the respective counts.
        int numberOfInfected = 0;
        int numberOfImmune = 0;
        
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                Animal animal = field.getObjectAt(row, col, Animal.class);
                Plant plant = field.getObjectAt(row, col, Plant.class);
                
                if(animal != null) 
                {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                    
                    // Update the infected and immune counts
                    if (animal.getIsInfected())
                    {
                        numberOfInfected++;
                    }
                    
                    if (animal.getIsImmune())
                    {
                        numberOfImmune++;
                    }
                }
                // Only show the plant if an animal is not present
                else if(plant != null) 
                {
                    fieldView.drawMark(col, row, getColor(plant.getClass()));
                }
                else 
                {
                    Color emptyColor = EMPTY_COLOR;
                    fieldView.drawMark(col, row, emptyColor);
                    fieldView.setBackground(emptyColor);
                }
                
                if (plant != null) 
                {
                   stats.incrementCount(plant.getClass()); 
                }
            }
        }
        
        stats.countFinished();
        
        infectedLabel.setText(INFECTED_PREFIX + numberOfInfected);
        immuneLabel.setText(IMMUNE_PREFIX + numberOfImmune);

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * 
     * @param field The field to check viability.
     *
     * @return True if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * @return Returns the properly formated time in 24 hr format.
     */
    private String getTimeString()
    {
        int time = Time.getTime();
        if (time >= 12){
            return time + ":00 ";
        }
        else {
            if(time<10){
                return "0" + time + ":00 ";
            }
            else {
                return time + ":00 ";
            }
        }
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     */
    @SuppressWarnings("serial")
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private double xScale, yScale;
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
         * 
         * @return Dimensions
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
                
                // Makes sure that the grid remains in proportion
                // (the grid squares remain as squares)
                if(xScale > yScale) {
                    xScale = yScale;
                }
                else if(xScale < yScale) {
                    yScale = xScale;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.

         * @param x X-position to draw the mark
         * @param y Y-position to draw the mark
         * @param color The colour to draw the mark
         */
        private void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect((int)(x * xScale), (int)(y * yScale) , (int)(xScale - 1.0), (int)(yScale - 1.0));
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         * 
         * @param g Graphics
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) 
            {
                Dimension currentSize = getSize();
                
                if(size.equals(currentSize)) 
                {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else 
                {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
