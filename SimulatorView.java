import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * This class is responsible solely for rendering — statistical data is
 * computed externally (by {@link FieldStats}) and passed in as a string.
 *
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private Color emptyColor = Color.white;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final String TIME_PREFIX = "Time: ";
    private JLabel stepLabel, populationLabel, infoLabel, timeLabel, weatherLabel;
    private FieldView fieldView;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        setTitle("Fox and Rabbit Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        timeLabel = new JLabel("", JLabel.CENTER);
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);

        setLocation(100, 50);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel topInfoPane = new JPanel(new BorderLayout());
            topInfoPane.add(stepLabel, BorderLayout.WEST);
            topInfoPane.add(timeLabel, BorderLayout.EAST);
            topInfoPane.add(infoLabel, BorderLayout.CENTER);

        JPanel bottomInfoPane = new JPanel(new BorderLayout());
            bottomInfoPane.add(populationLabel, BorderLayout.WEST);
            bottomInfoPane.add(weatherLabel, BorderLayout.EAST);

        contents.add(topInfoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(bottomInfoPane, BorderLayout.SOUTH);
        pack();
        setVisible(true);
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
     * @param step             Which iteration step it is.
     * @param field            The field whose contents are to be drawn.
     * @param timeOfDay        The current time tracker.
     * @param weather          The current weather.
     * @param populationSummary Pre-computed population summary string from FieldStats.
     */
    public void showStatus(int step, Field field, TimeTracker timeOfDay, Weather weather,
                           String populationSummary)
    {
        if (!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if (animal instanceof Displayable) {
                    Displayable item = (Displayable) animal;
                    fieldView.drawMark(col, row, item.getColor(), item.getBorderColor());
                } else {
                    fieldView.drawMark(col, row, emptyColor, null);
                }
            }
        }

        populationLabel.setText(POPULATION_PREFIX + populationSummary);
        fieldView.repaint();
        timeOfDay.incrementTime();
        timeLabel.setText(TIME_PREFIX + timeOfDay.getPrettyTime());
        weatherLabel.setText(WEATHER_PREFIX + weather.name());

        int smallTime = (int) Math.round(timeOfDay.normalisedTime() * 255);
        emptyColor = new Color(smallTime, smallTime, smallTime);
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
            super.setBackground(new Color(0, 0, 0));
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
            if (!size.equals(getSize())) {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if (yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint one grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color, Color borderColor)
        {
            if (borderColor != null) {
                g.setColor(borderColor);
            } else {
                g.setColor(Color.GRAY);
            }
            g.drawRect(x * xScale, y * yScale, xScale - 1, yScale - 1);

            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if (fieldImage != null) {
                Dimension currentSize = getSize();
                if (size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
