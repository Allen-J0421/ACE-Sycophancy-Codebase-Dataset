import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The simulation window: labels, buttons, and the embedded field canvas.
 * Pixel-level rendering is delegated to FieldRenderer.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class SimulatorView extends JFrame
{
    private final String STEP_PREFIX       = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";

    private JLabel stepLabel, population, infoLabel, weatherPropertiesLabel, dayLabel;

    private final FieldRenderer fieldRenderer;
    private SimulationOrchestrator orchestrator;

    // Threads for each method called by the buttons:
    private Thread runLongSimulationThread;
    private Thread resetThread;
    private Thread simulateOneStepThread;

    /**
     * Create a view of the given width and height.
     *
     * @param orchestrator The orchestrator that button actions are forwarded to.
     * @param height       The simulation's height.
     * @param width        The simulation's width.
     */
    public SimulatorView(SimulationOrchestrator orchestrator, int height, int width)
    {
        this.orchestrator = orchestrator;
        fieldRenderer = new FieldRenderer(height, width);

        setTitle("Australian Savannah Simulation");
        stepLabel  = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel  = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        dayLabel   = new JLabel("Day : 0", JLabel.CENTER);

        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridLayout(3, 0));

        JButton longSimButton = new JButton("4000 Steps");
        longSimButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (runLongSimulationThread != null && runLongSimulationThread.isAlive()) return;
                if (simulateOneStepThread   != null && simulateOneStepThread.isAlive())   return;
                runLongSimulationThread = new Thread(orchestrator::runLongSimulation);
                runLongSimulationThread.start();
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (resetThread             != null && resetThread.isAlive())             return;
                if (runLongSimulationThread != null && runLongSimulationThread.isAlive()) return;
                if (simulateOneStepThread   != null && simulateOneStepThread.isAlive())   return;
                resetThread = new Thread(orchestrator::reset);
                resetThread.start();
            }
        });

        JButton oneSimButton = new JButton("One Step");
        oneSimButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (simulateOneStepThread   != null && simulateOneStepThread.isAlive())   return;
                if (runLongSimulationThread != null && runLongSimulationThread.isAlive()) return;
                simulateOneStepThread = new Thread(orchestrator::simulateOneStep);
                simulateOneStepThread.start();
            }
        });

        buttonGrid.add(longSimButton);
        buttonGrid.add(resetButton);
        buttonGrid.add(oneSimButton);

        weatherPropertiesLabel = new JLabel("");
        updateWeatherPropertiesLabel();
        updateDayLabel();

        setLocation(100, 50);

        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel,              BorderLayout.WEST);
        infoPane.add(weatherPropertiesLabel, BorderLayout.EAST);
        infoPane.add(infoLabel,              BorderLayout.CENTER);
        infoPane.add(dayLabel);

        contents.add(infoPane,                   BorderLayout.NORTH);
        contents.add(fieldRenderer.getPanel(),   BorderLayout.CENTER);
        contents.add(population,                 BorderLayout.SOUTH);
        contents.add(buttonGrid,                 BorderLayout.WEST);

        pack();
        setVisible(true);
    }

    /**
     * Register a display color for the given actor class.
     *
     * @param actorClass The animal's Class object.
     * @param color      The color to be used for the given class.
     */
    public void setColor(Class actorClass, Color color)
    {
        fieldRenderer.setColor(actorClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Render the field and refresh all status labels.
     *
     * @param step  Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if (!isVisible()) setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);

        PopulationStats stats = Simulator.getPopulationStats();
        fieldRenderer.render(field, stats);

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails());
        updateWeatherPropertiesLabel();
        updateDayLabel();
    }

    private void updateWeatherPropertiesLabel()
    {
        weatherPropertiesLabel.setText(Simulator.getWeather().getCurrent().getDescription());
    }

    public void updateDayLabel()
    {
        dayLabel.setText("Day : " + TimeSystem.getCurrentDay());
    }
}
