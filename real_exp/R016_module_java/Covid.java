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
        addSpecies("Gazelle", "Lion", "Hyena");
        setProbability(0.000001);
        setInfectiousness(0.005);
        setDuration(8);
    }
}
