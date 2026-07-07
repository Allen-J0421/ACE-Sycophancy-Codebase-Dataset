import java.util.*;

/**
 * Default population strategy: iterates every cell of the field and
 * places a randomly chosen species based on per-species creation probabilities.
 *
 * @version 2022.03.02
 */
public class DefaultPopulationStrategy implements PopulationStrategy
{
    private static final Random rand = Randomizer.getRandom();
    private static final int HUNTER_LIMIT = 5;

    private final Map<Class<?>, Double> creationProbabilities;

    public DefaultPopulationStrategy(Map<Class<?>, Double> creationProbabilities)
    {
        this.creationProbabilities = creationProbabilities;
    }

    @Override
    public Map<Class<?>, Double> getCreationProbabilities()
    {
        return creationProbabilities;
    }

    /**
     * Clear the field and seed it with organisms according to the creation probabilities.
     * Hunter count is reset to zero at the start of each call so that repeated
     * resets always produce a fresh population up to HUNTER_LIMIT.
     */
    @Override
    public void populate(Field field, Environment environment, List<Actor> actors)
    {
        int hunterCount = 0;
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal.Gender sex = Randomizer.getRandomSex();
                Location location = new Location(row, col);

                if(rand.nextDouble() <= creationProbabilities.get(Grass.class)) {
                    actors.add(new Grass(field, location));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Deer.class)) {
                    actors.add(new Deer(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Coyote.class)) {
                    actors.add(new Coyote(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Wolf.class)) {
                    actors.add(new Wolf(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Eagle.class)) {
                    actors.add(new Eagle(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Mouse.class)) {
                    actors.add(new Mouse(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Hunter.class)) {
                    if(hunterCount < HUNTER_LIMIT) {
                        actors.add(new Hunter(field, location, environment));
                        hunterCount++;
                    }
                }
                // else leave the location empty.
            }
        }
    }
}
