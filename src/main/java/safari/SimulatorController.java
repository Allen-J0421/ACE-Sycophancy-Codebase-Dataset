package safari;

/**
 * Coordinates user actions, simulator state transitions and view updates.
 */
public final class SimulatorController implements SimulationActions
{
    private final Simulator simulator;
    private final SimulatorView view;

    public SimulatorController()
    {
        simulator = new Simulator();
        SimulationSnapshot snapshot = simulator.createSnapshot();
        view = new SimulatorView(snapshot.getField().getDepth(), snapshot.getField().getWidth(), this);
        view.showStatus(snapshot);
    }

    @Override
    public void stepRequested()
    {
        view.disableButton();
        try {
            simulator.simulateOneStep();
            view.showStatus(simulator.createSnapshot());
        }
        finally {
            view.enableButton();
        }
    }

    @Override
    public void resetRequested()
    {
        view.disableButton();
        try {
            simulator.reset();
            view.showStatus(simulator.createSnapshot());
        }
        finally {
            view.enableButton();
        }
    }

    @Override
    public void infectionRequested()
    {
        view.disableButton();
        try {
            simulator.introduceInfection();
            view.showStatus(simulator.createSnapshot());
        }
        finally {
            view.enableButton();
        }
    }
}
