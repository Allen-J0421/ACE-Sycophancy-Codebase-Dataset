
/**
 * Models a disease Leptospirosis which affects animals and can spread through water
 *
 * @version 2022.03.01
 */
public class Leptospirosis extends Disease
{
    /**
     * Constructor for Leptospirosis disease
     *
     * @param field The field currently occupied
     */
    public Leptospirosis(Field field)
    {
        super(field);
        addSpecies(SimulationConfiguration.LEPTOSPIROSIS_AFFECTED_SPECIES);
        setProbability(SimulationConfiguration.LEPTOSPIROSIS_PROBABILITY);
        setInfectiousness(SimulationConfiguration.LEPTOSPIROSIS_INFECTIOUSNESS);
        setDuration(SimulationConfiguration.LEPTOSPIROSIS_DURATION);
    }
}
