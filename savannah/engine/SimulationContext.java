package savannah.engine;

import java.util.Objects;

import savannah.config.SimulationConfig;
import savannah.model.Field;

/**
 * Shared simulation state used by model objects and the engine.
 */
public final class SimulationContext
{
    private final Field field;
    private final SimulationConfig config;
    private SimulationEngine engine;

    public SimulationContext(Field field, SimulationConfig config)
    {
        this.field = Objects.requireNonNull(field, "field");
        this.config = Objects.requireNonNull(config, "config");
    }

    void setEngine(SimulationEngine engine)
    {
        this.engine = engine;
    }

    public SimulationEngine getEngine()
    {
        return engine;
    }

    public Field getField()
    {
        return field;
    }

    public SimulationConfig getConfig()
    {
        return config;
    }
}
