import java.util.ArrayList;
import java.util.List;

/**
 * SimulationLogger that fans out every message to a list of delegates.
 * Use this to route log output to multiple destinations simultaneously
 * (e.g. console + file, or console + GUI panel).
 *
 * Example:
 *   CompositeLogger log = new CompositeLogger();
 *   log.addLogger(new ConsoleLogger());
 *   log.addLogger(myFileLogger);
 *   Simulator sim = new Simulator.Builder().logger(log).build();
 *
 * @version 2022.03.01
 */
public class CompositeLogger implements SimulationLogger
{
    private final List<SimulationLogger> loggers = new ArrayList<>();

    public void addLogger(SimulationLogger logger)
    {
        loggers.add(logger);
    }

    @Override
    public void info(String message)
    {
        for(SimulationLogger l : loggers) l.info(message);
    }

    @Override
    public void warn(String message)
    {
        for(SimulationLogger l : loggers) l.warn(message);
    }

    @Override
    public void error(String message)
    {
        for(SimulationLogger l : loggers) l.error(message);
    }
}
