/**
 * SimulationLogger implementation that writes to standard output.
 * This is the default logger used when no logger is specified on the Builder.
 *
 * @version 2022.03.01
 */
public class ConsoleLogger implements SimulationLogger
{
    @Override
    public void info(String message)  { System.out.println("[INFO]  " + message); }

    @Override
    public void warn(String message)  { System.out.println("[WARN]  " + message); }

    @Override
    public void error(String message) { System.out.println("[ERROR] " + message); }
}
