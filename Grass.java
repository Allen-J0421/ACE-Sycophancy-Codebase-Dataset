import java.util.List;

/**
 * A simple model of grass - a type of plant.
 *
 * @version 2022.02.28
 */
public class Grass extends Plant
{
    /**
     * Create a piece of grass. Grass can be created as a new born (age zero)
     * or with a random age and water level.
     *
     * @param randomAge If true, the grass will have random age and water level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(SpeciesRegistry.get("Grass"), randomAge, field, location);
    }

    /**
     * This is what the grass does each step of the simulation
     * - it grows and may reproduce.
     * @param newGrass A list to return newly grown grass
     */
    public void act(List<Actor> newGrass)
    {
        super.act(newGrass);
        decreaseWaterLevel();
        if(isAlive()) {
            giveBirth(newGrass);
            findWater();
        }
    }

    /**
     * Create a new piece of grass.
     */
    protected Plant createOffspring(Field field, Location location)
    {
        return new Grass(false, field, location);
    }
}
