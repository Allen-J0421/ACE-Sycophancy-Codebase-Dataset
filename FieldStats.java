/**
 * This class formats field snapshot data into user-facing statistics.
 * 
 * @version 2022.03.02
 */
public class FieldStats
{
    private final SimulationRulesEngine rules;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats(SimulationRulesEngine rules)
    {
        this.rules = rules;
    }

    /**
     * Get details of what is in the current snapshot.
     * @return A string describing the visible field state.
     */
    public String getPopulationDetails(FieldSnapshot snapshot, int step)
    {
        StringBuffer buffer = new StringBuffer();
        for(Species species : snapshot.getPresentSpecies()) {
            buffer.append(species.getDisplayName());
            buffer.append(": ");
            buffer.append(snapshot.getPopulationCount(species));
            buffer.append(' ');
        }
        buffer.append("Weather: " + rules.getCurrentWeather());
        buffer.append(" Time: " + rules.getTimeOfDay(step).getDisplayName());
        return buffer.toString();
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
