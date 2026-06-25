
/**
 * An animal disease that could kill its host.
 *
 * @version 2022.03.02
 */
public class Influenza extends AnimalDisease
{
    /**
     * Create an Influenza object, it also initialises it's contagiousness and lethality(deathProbability).
     */
    public Influenza()
    {
        contagiousness = 0.3f;
        deathProbability = 0.01f;
    }
}
