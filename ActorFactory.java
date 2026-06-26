import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creates actors and populates fields according to a simulation configuration.
 */
public class ActorFactory
{
    private static final List<Class<?>> CREATION_ORDER = List.of(
            Grass.class,
            Deer.class,
            Coyote.class,
            Wolf.class,
            Eagle.class,
            Mouse.class,
            Hunter.class
    );

    private final SimulationConfig config;
    private final RandomProvider randomProvider;

    public ActorFactory(RandomProvider randomProvider, SimulationConfig config)
    {
        this.randomProvider = randomProvider;
        this.config = config;
    }

    public List<Actor> populate(Field field)
    {
        List<Actor> actors = new ArrayList<>();
        int hunterCount = 0;

        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Actor actor = maybeCreateActor(field, location, hunterCount);
                if(actor != null) {
                    actors.add(actor);
                    if(actor instanceof Hunter) {
                        hunterCount++;
                    }
                }
            }
        }

        return actors;
    }

    public List<Actor> createGrassPatches(Field field, Environment environment)
    {
        Random rand = randomProvider.getRandom();
        if(environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return List.of();
        }

        double creationRate = config.getCreationProbability(Grass.class);
        List<Actor> grassActors = new ArrayList<>();
        for(Location location : field.getRandomFreePatches(creationRate)) {
            if(rand.nextDouble() <= creationRate) {
                grassActors.add(new Grass(field, location));
            }
        }

        return grassActors;
    }

    private Actor maybeCreateActor(Field field, Location location, int hunterCount)
    {
        Random rand = randomProvider.getRandom();
        for(Class<?> actorClass : CREATION_ORDER) {
            if(actorClass == Hunter.class && hunterCount >= config.getHunterLimit()) {
                continue;
            }

            if(rand.nextDouble() <= config.getCreationProbability(actorClass)) {
                return createActor(actorClass, field, location);
            }
        }

        return null;
    }

    private Actor createActor(Class<?> actorClass, Field field, Location location)
    {
        if(actorClass == Grass.class) {
            return new Grass(field, location);
        }
        if(actorClass == Hunter.class) {
            return new Hunter(field, location);
        }

        Animal.Gender sex = randomProvider.getRandom().nextBoolean()
                ? Animal.Gender.MALE
                : Animal.Gender.FEMALE;
        if(actorClass == Deer.class) {
            return new Deer(true, field, location, sex);
        }
        if(actorClass == Coyote.class) {
            return new Coyote(true, field, location, sex);
        }
        if(actorClass == Wolf.class) {
            return new Wolf(true, field, location, sex);
        }
        if(actorClass == Eagle.class) {
            return new Eagle(true, field, location, sex);
        }
        if(actorClass == Mouse.class) {
            return new Mouse(true, field, location, sex);
        }

        throw new IllegalArgumentException("Unsupported actor type: " + actorClass.getName());
    }
}
