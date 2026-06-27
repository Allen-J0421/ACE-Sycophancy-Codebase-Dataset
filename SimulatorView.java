import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method. Buttons have been introduced in order to change
 * the weather whenever desired.
 * 
 * @version 2022.03.02
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Species, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final SimulationRulesEngine rules;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, SimulationRulesEngine rules)
    {
        this.rules = rules;
        stats = new FieldStats(rules);
        colors = new LinkedHashMap<>();

        setTitle("Animal Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(0, 1));
        
        JButton SunnyButton = new JButton("Set Weather to Sunny");
        SunnyButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { SunnyButton(); }
                           });
        toolbar.add(SunnyButton);
        JButton RainyButton = new JButton("Set Weather to Rainy");
        RainyButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { RainyButton(); }
                           });
        toolbar.add(RainyButton);
        JButton FoggyButton = new JButton("Set Weather to Foggy");
        FoggyButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) { FoggyButton(); }
                           });
        toolbar.add(FoggyButton);
        
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(toolbar, BorderLayout.WEST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    private void SunnyButton()
    {
        rules.setWeather("Sunny");
    }
    
    private void RainyButton()
    {
        rules.setWeather("Rainy");
    }
    
    private void FoggyButton()
    {
        rules.setWeather("Foggy");
    }
    
    /**
     * Define a color to be used for a given species.
     * @param species The species to color.
     * @param color The color to be used for the given species.
     */
    public void setColor(Species species, Color color)
    {
        colors.put(species, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given species.
     */
    private Color getColor(Species species)
    {
        Color col = colors.get(species);
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
     * @param snapshot The field snapshot whose status is to be displayed.
     */
    public void showStatus(int step, FieldSnapshot snapshot)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        fieldView.preparePaint();

        for(int row = 0; row < snapshot.getDepth(); row++) {
            for(int col = 0; col < snapshot.getWidth(); col++) {
                Species species = snapshot.getSpeciesAt(row, col);
                if(species != null) {
                    fieldView.drawMark(col, row, getColor(species));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(snapshot, step));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(FieldSnapshot snapshot)
    {
        return stats.isViable(snapshot);
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
