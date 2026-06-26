import java.util.List;

/**
 * A class representing the shared characteristics between Prey
 *
 * @version 26/02/2022
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     * @param infected Initial state if the prey is infected or not.
     * @param immune   Initial state if the prey is immune or not.
     */
    protected Prey(Field field, Location location, boolean infected, boolean immune)
    {
        super(field, location, infected, immune);
        movementProbability = 0.75;
    }

    /**
     * @Override
     *
     * Prey sleep at night and otherwise delegate to Animal.act().
     *
     * @param newPrey A list to receive newly born prey.
     */
    public void act(List<LivingOrganism> newPrey)
    {
        if(Time.isNight())
        {
            return;
        }
        super.act(newPrey);
    }

    /**
     * @Override
     *
     * Look for an adjacent plant with no animal present.
     * Eats until full.
     *
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation()))
        {
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);
            Plant plant   = (Plant)  field.getObjectAt(where, Plant.class);

            if(animal == null && plant != null && plant.isAlive() && foodLevel < maxFoodLevel)
            {
                foodLevel += plant.beEaten();
                return where;
            }
        }
        return null;
    }
}
