/**
 * Central source of truth for simulation-wide services and shared configuration.
 */
public class SimulationContext
{
    private final RandomProvider randomProvider;
    private final SimulationConfig config;
    private final DiseaseService diseaseService;
    private final WeatherService weatherService;
    private final MovementService movementService;

    private Field field;
    private OrganismFactory organismFactory;
    private ActorService actorService;

    public SimulationContext(RandomProvider randomProvider, SimulationConfig config)
    {
        this.randomProvider = randomProvider;
        this.config = config;
        this.diseaseService = new DiseaseService(randomProvider);
        this.weatherService = new WeatherService(randomProvider);
        this.movementService = new MovementService(randomProvider);
    }

    public RandomProvider getRandomProvider()
    {
        return randomProvider;
    }

    public SimulationConfig getConfig()
    {
        return config;
    }

    public DiseaseService getDiseaseService()
    {
        return diseaseService;
    }

    public WeatherService getWeatherService()
    {
        return weatherService;
    }

    public MovementService getMovementService()
    {
        return movementService;
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

    public OrganismFactory getOrganismFactory()
    {
        return organismFactory;
    }

    public void setOrganismFactory(OrganismFactory organismFactory)
    {
        this.organismFactory = organismFactory;
    }

    public ActorService getActorService()
    {
        return actorService;
    }

    public void setActorService(ActorService actorService)
    {
        this.actorService = actorService;
    }
}
