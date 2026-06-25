/**
 * A plant disease that can kill its host.
 *
 * @version 2022.03.02
 */
public class Mould extends PlantDisease
{
    /**
     * Create a mould object, it also initialises it's contagiousness and lethality(deathProbability).
     */
    public Mould()
    {
        contagiousness = 0.3f;
        deathProbability = 0.01f;
    }
}
