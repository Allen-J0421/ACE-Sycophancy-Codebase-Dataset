import java.util.List;

/**
 * A class representing the shared characteristics of plants
 *
 * @version 01.03.22
 */

public abstract class Plant extends Organism
{
    /**
     * Create a new plant at a location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location) {
        super(field, location);
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly created plants.
     */
    abstract public void act(List<Plant> newPlants);

    /**
     * Generate a number representing the number of plants to produce,
     * if it can reproduce.
     * If it is raining, then the number of offspring is increased
     * @return The number of offspring (may be zero).
     */
    protected int breed(double REPRODUCING_PROBABILITY, int MAX_OFFSPRING_SIZE)
    {
        int offspring = 0;
        if (rand.nextDouble() <= (REPRODUCING_PROBABILITY)){
            if (getWeather()){
                offspring = rand.nextInt(MAX_OFFSPRING_SIZE) + 3;
            }
            else {
                offspring = rand.nextInt(MAX_OFFSPRING_SIZE);
            }
        }
        return offspring;
    }
}
