import java.util.List;

/**
 * A class representing the shared characteristics between predators
 *
 * @version 25/02/2022
 */
public abstract class Predator extends Animal
{
    // The likelihood of the predator catching its prey
    protected double preyCatchingProbability;

    /**
     * Create a new predator at location in field.
     *
     * @param field      The field currently occupied.
     * @param location   The location within the field.
     * @param isInfected Initial state if the predator is infected or not.
     * @param isImmune   Initial state if the predator is immune or not.
     */
    protected Predator(Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune);
        movementProbability = 0.8;
    }

    /**
     * @Override
     *
     * Predators are less active at night and in fog; otherwise delegates to Animal.act().
     *
     * @param newPredators A list to receive newly born predators.
     */
    public void act(List<LivingOrganism> newPredators)
    {
        if(Time.isNight() && rand.nextDouble() > 0.25)
        {
            return;
        }

        if(Weather.getWeather() == Weather.WeatherType.Foggy && rand.nextDouble() > 0.50)
        {
            return;
        }

        super.act(newPredators);
    }

    /**
     * @Override
     *
     * Look for prey adjacent to the current location.
     * Eats prey until full. May contract the prey's disease when eating.
     *
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation()))
        {
            Object animal = field.getObjectAt(where, Animal.class);

            if(animal instanceof Prey)
            {
                Prey prey = (Prey) animal;
                if(prey.isAlive() && foodLevel < maxFoodLevel && rand.nextDouble() < preyCatchingProbability)
                {
                    foodLevel += prey.beEaten();

                    if(prey.getIsInfected() && !immune && rand.nextDouble() <= diseaseSpreadProbability)
                    {
                        infected = true;
                    }

                    return where;
                }
            }
        }

        return null;
    }
}
