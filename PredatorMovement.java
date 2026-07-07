/**
 * Movement behaviour for predators: reduced activity at night (25% chance
 * to act) and in fog (50% chance to act); otherwise always active.
 */
public class PredatorMovement implements MovementBehaviour
{
    private static final double MOVEMENT_PROBABILITY = 0.8;

    @Override
    public boolean canActThisStep(SimRandom rand)
    {
        if (Time.isNight() && rand.nextDouble() > 0.25) return false;
        if (Weather.getWeather() == Weather.WeatherType.Foggy && rand.nextDouble() > 0.50) return false;
        return true;
    }

    @Override
    public double getMovementProbability()
    {
        return MOVEMENT_PROBABILITY;
    }
}
