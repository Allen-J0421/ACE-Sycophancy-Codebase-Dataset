import javax.swing.*;
import java.awt.*;

/**
 * A graphical view of the simulation.
 * Manages the window layout and the text labels (step, time, environment,
 * population). Delegates all grid-painting work to GridRenderer.
 *
 * @version 2022.03.02
 */
public class SimulatorView extends JFrame
{
    private final String STEP_PREFIX        = "Step: ";
    private final String TIME_DAY_PREFIX    = "Day: ";
    private final String TIME_HOUR_PREFIX   = "Hour: ";
    private final String TIME_OF_DAY_PREFIX = "Time: ";
    private final String POPULATION_PREFIX  = "Population: ";
    private final String WEATHER_PREFIX     = "Weather: ";

    private JLabel stepLabel, population, infoLabel, timeLabel, environmentLabel;

    // Handles all canvas drawing for the simulation grid.
    private GridRenderer renderer;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height (number of rows).
     * @param width  The simulation's width (number of columns).
     */
    public SimulatorView(int height, int width)
    {
        setTitle("Predator Prey Simulation");
        stepLabel        = new JLabel(STEP_PREFIX, JLabel.CENTER);
        timeLabel        = new JLabel(" ", JLabel.CENTER);
        environmentLabel = new JLabel(" ", JLabel.CENTER);
        infoLabel        = new JLabel(" ", JLabel.CENTER);
        population       = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        setLocation(100, 50);

        renderer = new GridRenderer(height, width);

        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel,        BorderLayout.WEST);
        infoPane.add(timeLabel,        BorderLayout.EAST);
        infoPane.add(environmentLabel, BorderLayout.CENTER);

        contents.add(infoPane,   BorderLayout.NORTH);
        contents.add(renderer,   BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    /**
     * Register a display color for a species class.
     * Forwarded to the underlying GridRenderer.
     *
     * @param speciesClass The runtime class of the species.
     * @param color        The color to use for that species.
     */
    public void setColor(Class speciesClass, Color color)
    {
        renderer.setColor(speciesClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Update the step counter label and render the current field state.
     * Population text must be supplied separately via {@link #updatePopulation(String)}.
     *
     * @param step  The current simulation step number.
     * @param field The field whose contents are to be painted.
     */
    public void showStatus(int step, Field field)
    {
        if (!isVisible()) {
            setVisible(true);
        }
        stepLabel.setText(STEP_PREFIX + step);
        renderer.render(field);
    }

    /**
     * Update the population label with the given pre-computed summary string.
     *
     * @param populationText A summary of current population counts.
     */
    public void updatePopulation(String populationText)
    {
        population.setText(POPULATION_PREFIX + populationText);
    }

    /**
     * Update the time label with the current day and hour.
     *
     * @param day  The current simulation day.
     * @param hour The current simulation hour.
     */
    public void updateTimeLabel(int day, int hour)
    {
        timeLabel.setText(TIME_DAY_PREFIX + day + " " + TIME_HOUR_PREFIX + hour);
    }

    /**
     * Update the environment label with the current weather and time of day.
     *
     * @param weather The current weather state.
     * @param time    The current time of day.
     */
    public void updateEnvironmentLabel(Weather weather, TimeOfDay time)
    {
        environmentLabel.setText(WEATHER_PREFIX + weather.getType().toString()
                + " " + TIME_OF_DAY_PREFIX + time);
    }
}
