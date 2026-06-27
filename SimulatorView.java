import java.awt.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 *
 * @version 2022/03/02
 */
public class SimulatorView extends JFrame
{
    private static final String STEP_PREFIX = "Step: ";
    private static final String POPULATION_PREFIX = "Population: ";
    private static final String DAYOFTIME_PREFIX = "Time: It's ";
    private static final String POPULATION_DIE_OF_DISEASE_PREFIX = "Population died of disease: ";

    private JLabel stepLabel, population, infoLabel, diseaseLabel;
    private final FieldView fieldView;
    private final CellColorStrategy cellColorStrategy;
    private final StatusTextStrategy statusTextStrategy;

    /**
     * Create a view of the given width and height using default strategies.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        this(height, width, new DefaultCellColorStrategy(), new DefaultStatusTextStrategy());
    }

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     * @param cellColorStrategy Strategy for rendering cell colors.
     * @param statusTextStrategy Strategy for formatting status text and viability.
     */
    public SimulatorView(int height, int width, CellColorStrategy cellColorStrategy, StatusTextStrategy statusTextStrategy)
    {
        this.cellColorStrategy = cellColorStrategy;
        this.statusTextStrategy = statusTextStrategy;

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
        cellColorStrategy.setColor(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param weather the object of weather class
     * @param oxygenLevel the oxygen level that is to be displayed in the current step.
     */
    public void showStatus(SimulationContext context)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + context.getStep());
        infoLabel.setText(statusTextStrategy.formatInfoText(context));
        diseaseLabel.setText(POPULATION_DIE_OF_DISEASE_PREFIX + Animal.populationDieOfDisease);
        cellColorStrategy.render(context, fieldView);
        population.setText(POPULATION_PREFIX + statusTextStrategy.formatPopulationText(context));
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(SimulationContext context)
    {
        return statusTextStrategy.isViable(context);
    }

    /**
     * Provide a graphical view of a rectangular field.
     */
    private class FieldView extends JPanel implements GridCanvas
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        private Dimension size;
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
         * Prepare for a new round of painting.
         */
        public void preparePaint()
        {
            if(!size.equals(getSize())) {
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
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed.
         */
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
