/**
 * This class formats field snapshot data into user-facing statistics.
 * 
 * @version 2022.03.02
 */
public class FieldStats
{
    // A shared weather object between the organisms and the simulator
    public static final Weather weather = Weather.getWeather();
    // The step of the simulation
    private int step;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats(int steps)
    {
        step = steps;
    }

    /**
     * Get details of what is in the current snapshot.
     * @return A string describing the visible field state.
     */
    public String getPopulationDetails(FieldSnapshot snapshot)
    {
        ++step;
        StringBuffer buffer = new StringBuffer();
        for(Species species : snapshot.getPresentSpecies()) {
            buffer.append(species.getDisplayName());
            buffer.append(": ");
            buffer.append(snapshot.getPopulationCount(species));
            buffer.append(' ');
        }
        buffer.append("Weather: " + weather.getCurrentWeather());
        buffer.append(" Time: " + getTimeOfDay());
        return buffer.toString();
    }
    
    private String getTimeOfDay()
    {
        if(step % 80<=55)
        {
            return "Day";
        }
        return "Night";
    }
    
    /**
     * Determine whether the simulation is still viable.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(FieldSnapshot snapshot)
    {
        return snapshot.getActiveSpeciesCount() > 1;
    }
}
