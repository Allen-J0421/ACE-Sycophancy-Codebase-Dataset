/**
 * The Infectable interface specifies the methods that an
 * animal who is able to be infected and is able to spread
 * infections must implement.
 *
 * @version 2022.03.02
 */
public interface Infectable
{
    // Probability of spreading disease
    double SPREAD_PROBABILITY = 0.1;
    
    /**
     * Decides whether the animal will spread their disease to another animal.
     */
    void spreadDisease();
    
    /**
     * If an animal is infected, reduce its health.
     */
    void illness();
}
