import java.awt.Insets;
import javax.swing.*;
/**
 * Bridges the simulation engine and the GUI by initializing control buttons,
 * wiring their event handlers to the simulator, and managing the dashboard lifecycle.
 * Decouples Simulator from direct knowledge of SimulatorView and Dashboard.
 *
 * @version 1.0
 */
public class SimulationController
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private final Simulator simulator;
    private final SimulatorView view;
    private Dashboard dashboard;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a SimulationController, builds the UI control buttons, wires their
     * event listeners to the simulator, and registers them with the view.
     *
     * @param simulator The simulation engine to drive.
     * @param view      The view to update and query.
     */
    public SimulationController(Simulator simulator, SimulatorView view)
    {
        this.simulator = simulator;
        this.view = view;
        initializeControls();
    }

    /*///////////////////////////////////////////////////////////////
                         EVENT HANDLING / UI INIT
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates the three control buttons, applies styling, wires action listeners,
     * and hands them to the view for integration into its sidebar.
     */
    private void initializeControls()
    {
        JButton simulateButton = new JButton("Simulate (1)");
        simulateButton.addActionListener(e -> simulator.simulateOneStep());
        simulateButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        simulateButton.setMargin(new Insets(4, 4, 4, 4));

        JButton simulateLong = new JButton("Simulate(1000)");
        simulateLong.addActionListener(e -> new Thread(simulator::runLongSimulation).start());
        simulateLong.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        simulateLong.setMargin(new Insets(4, 4, 4, 4));

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setMargin(new Insets(4, 4, 4, 4));
        dashboardButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        dashboardButton.addActionListener(e ->
            dashboard = new Dashboard(view.getCounters(), DiseaseHandler.count)
        );

        view.addControlButtons(simulateButton, simulateLong, dashboardButton);
    }

    /*///////////////////////////////////////////////////////////////
                         VIEW / DASHBOARD BRIDGE
    //////////////////////////////////////////////////////////////*/

    /**
     * Refreshes the view with the current simulation state and, if a dashboard
     * has been opened, updates it as well.
     *
     * @param step    The current simulation step.
     * @param field   The simulation field.
     * @param clock   The simulation clock.
     * @param weather The current weather.
     */
    public void updateView(int step, Field field, SimulatorClock clock, Weather weather)
    {
        view.showStatus(step, field, clock, weather);
        if(dashboard != null) {
            dashboard.updateDashboard();
        }
    }

    /**
     * Determines whether the simulation should continue to run.
     *
     * @param field The field to evaluate.
     * @return true if more than one species is alive.
     */
    public boolean isViable(Field field)
    {
        return view.isViable(field);
    }

    /**
     * Returns the view managed by this controller.
     *
     * @return the SimulatorView.
     */
    public SimulatorView getView()
    {
        return view;
    }
}
