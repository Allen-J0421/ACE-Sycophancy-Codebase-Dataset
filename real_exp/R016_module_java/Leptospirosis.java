
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
        addSpecies("Lion", "Hyena", "Mouse", "Gazelle", "Lake");
        setProbability(0.0000008);
        setInfectiousness(0.001);
        setDuration(10);
    }

}
