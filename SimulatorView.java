import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;


/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2022/03/02
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    
    // Colour used for underwater storm
    private static final Color UNDERWATERSTORM_COLOR = Color.blue;
    
    // Colour used for objects that are infected by disease;
    private static final Color INFECTED_COLOR = Color.green;
    

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DAYOFTIME_PREFIX = "Time: It's ";
    private final String POPULATION_DIE_OF_DISEASE_PREFIX = "Population died of disease: ";
    
    
    private JLabel stepLabel, population, infoLabel, diseaseLabel;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
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

        setTitle("Underwater Environment Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(DAYOFTIME_PREFIX + " ", JLabel.CENTER);
        diseaseLabel = new JLabel(POPULATION_DIE_OF_DISEASE_PREFIX, JLabel.CENTER);
        
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(diseaseLabel, BorderLayout.EAST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
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
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param weather the object of weather class
     * @param oxygenLevel the oxygen level that is to be displayed in the current step.
     */
    public void showStatus(int step, Field field, boolean timeOfDay, Weather weather, double oxygenLevel)
    {
         if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        infoLabel.setText("It is: " + (timeOfDay? "daytime": "night ") 
                            + "        Oxygen Level: " + (int)(oxygenLevel * 100) + "%"
                            + "        Storm: " + (weather.getStormStart()? "exists": "subsides"));
        diseaseLabel.setText(POPULATION_DIE_OF_DISEASE_PREFIX + Animal.populationDieOfDisease);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object creature = field.getObjectAt(row, col);
                if(creature != null && weather.getStormStart() == false) {
                    stats.incrementCount(creature.getClass());
                    if(creature instanceof Animal){
                     Animal ani = (Animal)creature;
                        if(ani.getIsInfected() && !ani.getIsImmuned())
                            fieldView.drawMark(col, row, INFECTED_COLOR);
                        else 
                            fieldView.drawMark(col, row, getColor(creature.getClass()));
                    }
                    else
                        fieldView.drawMark(col, row, getColor(creature.getClass()));
                }
                else if(creature != null && weather.getStormStart() == true) {
                    stats.incrementCount(creature.getClass());
                    fieldView.drawMark(col, row, getColor(creature.getClass())); 
                    drawStormColor(field, weather);
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
     * Draw the colour of the storm, and the range of the storm is given by storm scope of 
     * the object of Weather class.
     * 
     * @param field The field where the weather might happen and to be displayed.
     * @param weather the object of Weather class
     */
    private void drawStormColor(Field field, Weather weather) {
        List<Location> stormLocation = field.adjacentLocationsIncludingSelf(weather.getRandomLocation(),weather.getStormScope());
        Iterator<Location> it = stormLocation.iterator();
        while(it.hasNext() ) {
            Location location = it.next();
            fieldView.drawMark(location.getCol(),location.getRow(),getColor(Weather.class));
        }
        
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
