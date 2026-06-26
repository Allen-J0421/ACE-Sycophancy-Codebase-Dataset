import java.util.List;
import java.util.Random;

/**
 * Shared lifecycle logic for plant species.
 */
public abstract class Plant extends Creature
{
    private static final Random RAND = Randomizer.getRandom();

    private final PlantProfile profile;
    private int age;

    public Plant(boolean randomAge, Field field, Location location, PlantProfile profile)
    {
        super(field, location);
        this.profile = profile;
        age = randomAge ? RAND.nextInt(profile.getMaxAge()) : 0;
    }

    public final double act(List<Creature> newPlants, SimulationContext context)
    {
        incrementAge();
        if(context.getOxygenLevel() < profile.getOxygenRequiredAtNight()) {
            setDead();
            return 0;
        }

        if(isAlive() && age >= profile.getMatureAge()) {
            propagate(newPlants);
        }

        if(context.isAtDayTime()) {
            return profile.getOxygenGeneratedAtDay();
        }
        return -profile.getOxygenRequiredAtNight();
    }

    protected int getAge()
    {
        return age;
    }

    private void incrementAge()
    {
        age++;
        if(age > profile.getMaxAge()) {
            setDead();
        }
    }

    private void propagate(List<Creature> newPlants)
    {
        createAdjacentCreatures(newPlants, profile.getMaxPropagateSize(), profile::createYoung);
    }
}
