import java.util.ArrayList;

/**
 * Models a hypothetical disease which affects animals
 *
 * @version 2022.03.01
 */
public class Covid extends Disease
{
    /**
     * Constructor for Covid disease
     *
     * @param field The field currently occupied
     */
    public Covid(Field field)
    {
        super(field);
        addSpecies(SimulationConfiguration.COVID_AFFECTED_SPECIES);
        setProbability(SimulationConfiguration.COVID_PROBABILITY);
        setInfectiousness(SimulationConfiguration.COVID_INFECTIOUSNESS);
        setDuration(SimulationConfiguration.COVID_DURATION);
    }
}
