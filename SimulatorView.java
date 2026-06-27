import java.awt.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * This class is responsible solely for component layout and label updates.
 * Coordinate scaling is handled by {@link GridScaler}; off-screen rendering
 * is handled by the {@link FieldView} inner class.
 *
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    private Color emptyColor = Color.white;

    private final String STEP_PREFIX       = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String WEATHER_PREFIX    = "Weather: ";
    private final String TIME_PREFIX       = "Time: ";

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
        stepLabel      = new JLabel(STEP_PREFIX,       JLabel.CENTER);
        infoLabel      = new JLabel("  ",              JLabel.CENTER);
        timeLabel      = new JLabel("",                JLabel.CENTER);
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        weatherLabel   = new JLabel(WEATHER_PREFIX,    JLabel.CENTER);

        setLocation(100, 50);
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel topInfoPane = new JPanel(new BorderLayout());
        topInfoPane.add(stepLabel,  BorderLayout.WEST);
        topInfoPane.add(timeLabel,  BorderLayout.EAST);
        topInfoPane.add(infoLabel,  BorderLayout.CENTER);

        JPanel bottomInfoPane = new JPanel(new BorderLayout());
        bottomInfoPane.add(populationLabel, BorderLayout.WEST);
        bottomInfoPane.add(weatherLabel,    BorderLayout.EAST);

        contents.add(topInfoPane,    BorderLayout.NORTH);
        contents.add(fieldView,      BorderLayout.CENTER);
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
     * @param step              Which iteration step it is.
     * @param field             The field whose contents are to be drawn.
     * @param timeOfDay         The current time tracker.
     * @param weather           The current weather.
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

        int brightness = (int) Math.round(timeOfDay.normalisedTime() * 255);
        emptyColor = new Color(brightness, brightness, brightness);
    }

    // -------------------------------------------------------------------------

    /**
     * A Swing panel that maintains an off-screen image buffer and paints
     * individual grid cells onto it. Coordinate mapping (grid → pixels) is
     * fully delegated to {@link GridScaler}.
     */
    private class FieldView extends JPanel
    {
        private final GridScaler scaler;
        private Dimension        lastSize = new Dimension(0, 0);
        private Image            offscreenImage;
        private Graphics         offscreenGraphics;

        FieldView(int height, int width)
        {
            scaler = new GridScaler(width, height);
            super.setBackground(new Color(0, 0, 0));
        }

        @Override
        public Dimension getPreferredSize()
        {
            return scaler.getPreferredSize();
        }

        /**
         * Prepare for a new round of painting. Recreates the off-screen
         * buffer and refreshes the scale factors if the component has been
         * resized since the last call.
         */
        void preparePaint()
        {
            Dimension current = getSize();
            if (!lastSize.equals(current)) {
                lastSize      = current;
                offscreenImage    = createImage(current.width, current.height);
                offscreenGraphics = offscreenImage.getGraphics();
                scaler.update(current);
            }
        }

        /**
         * Paint one grid cell in the given fill and border colors.
         * @param gridCol     column index of the cell
         * @param gridRow     row index of the cell
         * @param color       fill color
         * @param borderColor border color, or {@code null} for the default gray
         */
        void drawMark(int gridCol, int gridRow, Color color, Color borderColor)
        {
            Rectangle bounds = scaler.toCellBounds(gridCol, gridRow);

            offscreenGraphics.setColor(borderColor != null ? borderColor : Color.GRAY);
            offscreenGraphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

            offscreenGraphics.setColor(color);
            offscreenGraphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            if (offscreenImage != null) {
                Dimension current = getSize();
                if (lastSize.equals(current)) {
                    g.drawImage(offscreenImage, 0, 0, null);
                } else {
                    g.drawImage(offscreenImage, 0, 0, current.width, current.height, null);
                }
            }
        }
    }
}
