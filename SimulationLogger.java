/**
 * Logging interface for the simulation.
 *
 * Decouples log output from the classes that produce log messages.
 * Register one or more implementations via Simulator.Builder.logger();
 * use CompositeLogger to fan out to multiple outputs simultaneously.
 *
 * @version 2022.03.01
 */
public interface SimulationLogger
{
    void info(String message);
    void warn(String message);
    void error(String message);
}
