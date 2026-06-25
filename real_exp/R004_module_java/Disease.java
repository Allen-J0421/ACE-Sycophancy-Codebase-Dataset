import java.util.Random;

/**
 * This class models disease in the simulation. 
 * It stores details about the disease such as 
 * lethality rate, propagation rate and the method of transmission.
 *
 * @version 2022.03.2
*/
public class Disease 
{
    private Random rand = Randomizer.getRandom();
    // The propagation rate of the disease
    private static double PROPAGATION_RATE = 0.0;
    // the lethality rate of the disease
    private static double LETHALITY_RATE = 0.0;
    // the transmission route of the disease 
    private DiseaseType PROPAGATION_MODE;

    /**
     * Creates a new disease with random propagation and lethality rates.
     * double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
     * https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
     */
    public Disease() 
    {
        PROPAGATION_RATE = 0.6 * rand.nextDouble(); // max prop. rate is 60%
        LETHALITY_RATE = 0.4 * rand.nextDouble(); // max lethality rate is 40%
        PROPAGATION_MODE = DiseaseType.values()[rand.nextInt(DiseaseType.values().length)];
    }
    
    /**
     * Create a new disease with given propagation and lethality rates.
     * @param propRate The propagation rate of the disease.
     * @param lethalRate The lethality rate of the disease.
     * @param propMode The propagation mode of the disease. 
     */
    public Disease(double propRate, double lethalRate, DiseaseType propMode)
    {
        PROPAGATION_RATE = propRate;
        LETHALITY_RATE = lethalRate;
        PROPAGATION_MODE = propMode;
    }

    /**
     * Returns the propagation rate of the disease
     */
    public double getPropagationRate() 
    {
        return PROPAGATION_RATE;
    }

    /**
     * Returns the lethality rate of the disease
     */
    public double getLethalityRate() 
    {
        return LETHALITY_RATE;
    }

    /**
     * Returns the disease's mode of transmission
     */
    public DiseaseType getDiseaseType() 
    {
        return PROPAGATION_MODE;
    }
}
