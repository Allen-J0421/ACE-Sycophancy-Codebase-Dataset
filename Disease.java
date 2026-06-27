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
    private String name;
    private boolean spreadByBirth;
    private boolean spreadByEating; 
    //actors name as the string, and the disease multiplier as the double
    private Map<String, Double> actorsAffected;
    //actors name as string, the probability of the animal starting with the disease as the double
    private Map<String, Double> startingActors;
    
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
     * @return Map<String,Double> of the actors affected
     */
    public Map<String, Double> getActorsAffectedMap()
    {
        return actorsAffected;
    }
    
     /**
     * returns the map of the actors that start with the disease
     * @return Map<String,Double> of the actors that start with it
     */
    public Map<String, Double> getStartingActorsMap()
    {
        return startingActors;
    }
}
