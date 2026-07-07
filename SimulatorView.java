import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * High-level GUI window for the simulation. Owns the status label bar and
 * the control buttons, and delegates rendering to FieldRenderer and
 * statistics display to PopulationStatsPanel.
 *
 * @version 2022.03.3
 */
public class SimulatorView extends JFrame
{
    private final String STEP_PREFIX    = "Step: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final String TIME_PREFIX    = "Time: ";

    private JLabel stepLabel, infoLabel, weatherLabel, timeLabel;
    private JButton oneStepButton, stopButton, longButton, resetButton;

    private final FieldRenderer renderer;
    private final PopulationStatsPanel statsPanel;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        renderer   = new FieldRenderer(height, width);
        statsPanel = new PopulationStatsPanel(renderer);

        setTitle("Prey and predator simulation");

        stepLabel    = new JLabel(STEP_PREFIX,    JLabel.CENTER);
        infoLabel    = new JLabel("  ",           JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        timeLabel    = new JLabel(TIME_PREFIX,    JLabel.CENTER);

        oneStepButton = new JButton("Simulate 1 step");
        stopButton    = new JButton("Stop simulation");
        longButton    = new JButton("Play 4000 steps");
        resetButton   = new JButton("Reset simulation");

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.add(oneStepButton);
        toolbar.add(longButton);
        toolbar.add(stopButton);
        toolbar.add(resetButton);
        toolbar.add(statsPanel);

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel,    BorderLayout.WEST);
        infoPane.add(infoLabel,    BorderLayout.CENTER);
        infoPane.add(weatherLabel, BorderLayout.CENTER);
        infoPane.add(timeLabel,    BorderLayout.EAST);

        Container contents = getContentPane();
        contents.add(infoPane,  BorderLayout.NORTH);
        contents.add(renderer,  BorderLayout.CENTER);
        contents.add(toolbar,   BorderLayout.EAST);

        setLocation(0, 0);
        pack();
        setVisible(false);

        statsPanel.syncColorsToRenderer();
    }

    /** Register an action listener on the one-step button. */
    public void addOneStepButtonListener(ActionListener listener) { oneStepButton.addActionListener(listener); }

    /** Register an action listener on the reset button. */
    public void addResetButtonListener(ActionListener listener)   { resetButton.addActionListener(listener); }

    /** Register an action listener on the stop button. */
    public void addStopButtonListener(ActionListener listener)    { stopButton.addActionListener(listener); }

    /** Register an action listener on the long-run button. */
    public void addLongButtonListener(ActionListener listener)    { longButton.addActionListener(listener); }

    /** Set the display color for a species class. */
    public void setColor(Class cls, Color color) { renderer.setColor(cls, color); }

    /** Display a short information label at the top of the window. */
    public void setInfoText(String text) { infoLabel.setText(text); }

    /**
     * Show the current status of the field: update labels, render the grid,
     * refresh population counts, and sync checkbox-driven colors.
     *
     * @param step    The current simulation step.
     * @param weather Current weather string.
     * @param time    Current time-of-day string.
     * @param field   The field to render.
     */
    public void showStatus(int step, String weather, String time, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        weatherLabel.setText(WEATHER_PREFIX + weather);
        timeLabel.setText(TIME_PREFIX + time);

        statsPanel.getStats().reset();
        renderer.render(field, statsPanel.getStats());
        statsPanel.updateDisplay();
        statsPanel.syncColorsToRenderer();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return statsPanel.isViable();
    }
}
