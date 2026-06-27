import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Disease that can spread between animals by animals eating infected actors and through birth meaning the
 * parents and and child get each others' diseases.
 * The disease affects different animals with different multipliers, and the disease can be created
 * onto an actor during the start of the simulation.
 *
 * @version 27.02.22
 */
public class Disease
{
    private final String name;
    private final boolean spreadByBirth;
    private final boolean spreadByEating;
    // actor class mapped to disease severity multiplier (0.0 = fatal, 1.0 = carrier only)
    private Map<Class<? extends Actor>, Double> actorsAffected;
    // actor class mapped to probability of starting with this disease
    private Map<Class<? extends Actor>, Double> startingActors;

    /**
     * Creates the disease and sets it name, and how it spreads, who it can spread to and
     * which actor it starts with.
     * @param name The name of the disease
     * @param spreadByBirth Whether or not the disease is spread by birth.
     * @param spreadByEating Whether or not the disease is spread by eating.
     */
    public Disease(String name, boolean spreadByBirth,boolean spreadByEating)
    {
        this.name = name;
        this.spreadByBirth = spreadByBirth;
        this.spreadByEating = spreadByEating;
        actorsAffected = new HashMap<>();
        startingActors = new HashMap<>();
    }

    /**
     * Returns the String name of the disease.
     * @return The String name of the disease.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Used to return if the disease is spread by birth,
     * the child gets their parents disease and the mate also gets the disease during
     * birth
     * @return true if it does spread by birth
     */
    public boolean isSpreadByBirth()
    {
        return spreadByBirth;
    }

    /**
     * Used to return if the disease is spread by eating,
     * predators can be infected by eating an infected actor
     * @return true if it does spread by eating
     */
    public boolean isSpreadByEating()
    {
        return spreadByEating;
    }

    /**
     * returns the map of the actors affected by the disease
     * @return Map of actor class to severity multiplier
     */
    public void addAffectedActor(Class<? extends Actor> actorClass, double severity)
    {
        actorsAffected.put(actorClass, severity);
    }

    public void addStartingActor(Class<? extends Actor> actorClass, double probability)
    {
        startingActors.put(actorClass, probability);
    }

    public Map<Class<? extends Actor>, Double> getActorsAffectedMap()
    {
        return Collections.unmodifiableMap(actorsAffected);
    }

    /**
     * returns the map of the actors that start with the disease
     * @return Map of actor class to starting probability
     */
    public Map<Class<? extends Actor>, Double> getStartingActorsMap()
    {
        return Collections.unmodifiableMap(startingActors);
    }
}