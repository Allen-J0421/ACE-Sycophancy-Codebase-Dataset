/**
 * No-op SimulationLogger that discards all messages.
 * Useful in tests or batch runs where console output is unwanted.
 *
 * @version 2022.03.01
 */
public class SilentLogger implements SimulationLogger
{
    @Override public void info(String message)  {}
    @Override public void warn(String message)  {}
    @Override public void error(String message) {}
}
