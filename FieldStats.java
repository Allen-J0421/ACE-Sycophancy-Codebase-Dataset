/**
 * This class formats field snapshot data into user-facing statistics.
 * 
 * @version 2022.03.02
 */
public class FieldStats
{
    /**
     * Get details of what is in the current simulation state.
     * @return A string describing the visible field state.
     */
    public String getPopulationDetails(SimulationState state)
    {
        FieldSnapshot snapshot = state.getFieldSnapshot();
        StringBuffer buffer = new StringBuffer();
        for(Species species : snapshot.getPresentSpecies()) {
            buffer.append(species.getDisplayName());
            buffer.append(": ");
            buffer.append(snapshot.getPopulationCount(species));
            buffer.append(' ');
        }
        buffer.append("Weather: " + state.getCurrentWeather());
        buffer.append(" Time: " + state.getTimeOfDay().getDisplayName());
        return buffer.toString();
    }
}
