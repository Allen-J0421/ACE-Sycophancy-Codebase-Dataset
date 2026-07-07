import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages the pure simulation state and time-stepping logic: the creature
 * population, field, environment (oxygen, weather, disease), and the act
 * loop. Does not interact with the view. All tunable parameters are supplied
 * via a SimulationConfig so they can be varied without changing engine logic.
 *
 * @version 2022/03/02
 */
public class SimulationEngine
{
    private final SimulationConfig config;

    private List<Creature> creatures;
    private Field field;
    private int step;
    private DiseaseManager diseaseManager;
    private Weather weather;
    private double oxygenLevel;

    /**
     * Create a simulation engine using the supplied configuration.
     * @param config Immutable parameters for grid size and spawn probabilities.
     */
    public SimulationEngine(SimulationConfig config)
    {
        this.config = config;
        creatures = new ArrayList<>();
        field = new Field(config.depth, config.width);
        weather = new Weather(field);
        diseaseManager = new DiseaseManager();
        oxygenLevel = 1;
        Animal.populationDieOfDisease = 0;
        populate();
    }

    public SimulationConfig getConfig() { return config; }
    public Field getField() { return field; }
    public int getStep() { return step; }
    public Weather getWeather() { return weather; }
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

        diseaseManager.trySpread(creatures, step);

        List<Creature> newCreatures = new ArrayList<>();
        for (Iterator<Creature> it = creatures.iterator(); it.hasNext(); ) {
            Creature creature = it.next();
            totalOxygenInvolved += creature.act(newCreatures, timeOfDay(), oxygenLevel, diseaseManager.getParams(), step);
            if (!creature.isAlive()) {
                it.remove();
            }
        }

        if (Randomizer.getRandom().nextDouble() <= config.stormHappenProbability) {
            weather.underwaterStorm(3);
            weather.setStormStart(true);
        } else {
            weather.setStormStart(false);
        }

        oxygenLevel += totalOxygenInvolved;

        if (diseaseManager.getIsSpread()) {
            diseaseManager.setIsSpread(diseaseManager.isStillActive(creatures));
        }

        creatures.addAll(newCreatures);
    }

    /**
     * Reset the simulation state to a fresh starting configuration.
     */
    public void reset()
    {
        step = 0;
        creatures.clear();
        oxygenLevel = 1;
        Animal.populationDieOfDisease = 0;
        populate();
    }

    /**
     * Randomly populate the field with Salmon, Seaweed, Shark and Whale.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= config.salmonCreationProbability) {
                    Location location = new Location(row, col);
                    Salmon salmon = new Salmon(true, field, location);
                    creatures.add(salmon);
                } else if (rand.nextDouble() <= config.codCreationProbability) {
                    Location location = new Location(row, col);
                    Cod cod = new Cod(true, field, location);
                    creatures.add(cod);
                } else if (rand.nextDouble() <= config.seaweedCreationProbability) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    creatures.add(seaweed);
                } else if (rand.nextDouble() <= config.sharkCreationProbability) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    creatures.add(shark);
                } else if (rand.nextDouble() <= config.whaleCreationProbability) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    creatures.add(whale);
                }
            }
        }
    }

}
