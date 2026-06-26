import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registry of factories used to assemble core simulation services.
 */
public class SimulationRegistry
{
    private Function<SimulationConfig, Field> fieldFactory;
    private Supplier<EnvironmentController> environmentControllerFactory;
    private Supplier<FieldStats> fieldStatsFactory;

    public SimulationRegistry()
    {
        fieldFactory = config -> new Field(config.getDepth(), config.getWidth());
        environmentControllerFactory = EnvironmentController::new;
        fieldStatsFactory = FieldStats::new;
    }

    public SimulationRegistry registerFieldFactory(Function<SimulationConfig, Field> fieldFactory)
    {
        this.fieldFactory = Objects.requireNonNull(fieldFactory, "fieldFactory");
        return this;
    }

    public SimulationRegistry registerEnvironmentControllerFactory(Supplier<EnvironmentController> environmentControllerFactory)
    {
        this.environmentControllerFactory = Objects.requireNonNull(environmentControllerFactory, "environmentControllerFactory");
        return this;
    }

    public SimulationRegistry registerFieldStatsFactory(Supplier<FieldStats> fieldStatsFactory)
    {
        this.fieldStatsFactory = Objects.requireNonNull(fieldStatsFactory, "fieldStatsFactory");
        return this;
    }

    public Field createField(SimulationConfig config)
    {
        return Objects.requireNonNull(fieldFactory.apply(config), "fieldFactory returned null");
    }

    public EnvironmentController createEnvironmentController()
    {
        return Objects.requireNonNull(environmentControllerFactory.get(), "environmentControllerFactory returned null");
    }

    public FieldStats createFieldStats()
    {
        return Objects.requireNonNull(fieldStatsFactory.get(), "fieldStatsFactory returned null");
    }
}
