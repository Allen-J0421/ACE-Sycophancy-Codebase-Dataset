import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the pure simulation state and time-stepping logic: environment
 * (oxygen, weather, disease) and the act loop. Population state is delegated
 * to PopulationManager. Does not interact with the view. All tunable
 * parameters are supplied via a SimulationConfig.
 *
 * @version 2022/03/02
 */
public class SimulationEngine
{
    private final SimulationConfig config;

    private final Field field;
    private PopulationManager populationManager;
    private DiseaseManager diseaseManager;
    private WeatherManager weatherManager;
    private int step;
    private double oxygenLevel;

    /**
     * Create a simulation engine using the supplied configuration.
     * @param config Immutable parameters for grid size and spawn probabilities.
     */
    public SimulationEngine(SimulationConfig config)
    {
        this.config = config;
        field = new Field(config.depth, config.width);
        populationManager = new PopulationManager();
        weatherManager = new WeatherManager(field, config.stormHappenProbability);
        diseaseManager = new DiseaseManager();
        oxygenLevel = 1;
        Animal.populationDieOfDisease = 0;
        populationManager.populate(field, config);
    }

    public SimulationConfig getConfig() { return config; }
    public Field getField() { return field; }
    public int getStep() { return step; }
    public Weather getWeather() { return weatherManager.getWeather(); }
    public double getOxygenLevel() { return oxygenLevel; }

    /**
     * 5 steps is considered as day time followed by 5 steps considered as night.
     * @return true if currently day time, false if night time.
     */
    public boolean timeOfDay()
    {
        return (step % 10) < 5;
    }

    /**
     * Advance the simulation by one step: run the act loop, update weather,
     * oxygen level, and disease state.
     */
    public void stepOnce()
    {
        step++;

        double totalOxygenInvolved = 0;

        List<Creature> creatures = populationManager.getCreatures();
        diseaseManager.trySpread(creatures, step);

        List<Creature> newCreatures = new ArrayList<>();
        for (Iterator<Creature> it = creatures.iterator(); it.hasNext(); ) {
            Creature creature = it.next();
            totalOxygenInvolved += creature.act(newCreatures, timeOfDay(), oxygenLevel, diseaseManager.getParams(), step);
            if (!creature.isAlive()) {
                it.remove();
            }
        }

        weatherManager.tryStorm();

        oxygenLevel += totalOxygenInvolved;

        if (diseaseManager.getIsSpread()) {
            diseaseManager.setIsSpread(diseaseManager.isStillActive(creatures));
        }

        populationManager.mergeNewborns(newCreatures);
    }

    /**
     * Reset the simulation state to a fresh starting configuration.
     */
    public void reset()
    {
        step = 0;
        populationManager.clear();
        oxygenLevel = 1;
        Animal.populationDieOfDisease = 0;
        populationManager.populate(field, config);
    }
}
