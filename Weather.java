import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Models weather in the simulation.
 * Each step, one weather condition may activate probabilistically.
 * The per-actor effect is delegated entirely to the active WeatherCondition,
 * eliminating the conditional logic that previously lived here.
 *
 * @version 2022.03.01
 */
public class Weather implements Actor
{
    // Indicates whether new water sources need to be generated
    private boolean generateWater = false;
    // The simulator (used to obtain the current step for duration loops)
    private Simulator simulator;

    /**
     * Constructor for objects of class Weather
     */
    public Weather(Simulator simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Probabilistically select a weather condition and apply it to all actors.
     * @param actorsList The current list of actors in the simulation.
     */
    public void act(List<Actor> actorsList)
    {
        WeatherCondition condition = selectCondition(new Random());
        if(condition != null) {
            applyCondition(condition, actorsList);
            if(condition.generatesWater()) {
                generateWater = true;
            }
        }
    }

    /**
     * @return Whether or not the simulator should generate new water sources
     * (set to true the first time rain occurs).
     */
    public boolean generateNewWater()
    {
        return generateWater;
    }

    /**
     * Choose a WeatherCondition based on the simulator's runtime configuration.
     * Returns null when no condition triggers this step.
     */
    private WeatherCondition selectCondition(Random rand)
    {
        SimulatorConfig config = simulator.getConfig();
        if(rand.nextDouble() <= config.getRainProbability()) {
            return new RainCondition();
        }
        else if(rand.nextDouble() <= config.getFogProbability()) {
            return new FogCondition();
        }
        else if(rand.nextDouble() <= config.getHeatwaveProbability()) {
            return new HeatwaveCondition();
        }
        return null;
    }

    /**
     * Apply a condition to every actor for its configured duration.
     * Preserves the original iteration structure exactly.
     */
    private void applyCondition(WeatherCondition condition, List<Actor> actorsList)
    {
        int current = simulator.getStep();
        int stop = current + condition.getDuration();
        while(current <= stop) {
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                condition.applyEffect(it.next());
                current++;
            }
        }
    }
}
