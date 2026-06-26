package safari;

/**
 * Application entry point for the safari simulation.
 */
public final class Main
{
    private Main()
    {
    }

    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(SimulatorController::new);
    }
}
