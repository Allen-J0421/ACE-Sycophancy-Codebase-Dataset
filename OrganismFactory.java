import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Centralizes instantiation rules for all concrete organism types.
 */
public class OrganismFactory
{
    private static final List<Class<? extends Organism>> CREATION_ORDER = List.of(
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
    private final DiseaseService diseaseService;
    private final Map<Class<? extends Organism>, OrganismCreator<? extends Organism>> creators;

    public OrganismFactory(RandomProvider randomProvider, SimulationConfig config, DiseaseService diseaseService)
    {
        this.randomProvider = randomProvider;
        this.config = config;
        this.diseaseService = diseaseService;
        this.creators = Map.ofEntries(
                Map.entry(Grass.class, (field, location, options) -> new Grass(field, location)),
                Map.entry(Hunter.class, (field, location, options) -> new Hunter(field, location)),
                Map.entry(Deer.class, (field, location, options) -> new Deer(options.randomAge(), field, location, requireSex(options))),
                Map.entry(Coyote.class, (field, location, options) -> new Coyote(options.randomAge(), field, location, requireSex(options))),
                Map.entry(Wolf.class, (field, location, options) -> new Wolf(options.randomAge(), field, location, requireSex(options))),
                Map.entry(Eagle.class, (field, location, options) -> new Eagle(options.randomAge(), field, location, requireSex(options))),
                Map.entry(Mouse.class, (field, location, options) -> new Mouse(options.randomAge(), field, location, requireSex(options)))
        );
    }

    public List<Actor> populate(Field field)
    {
        List<Actor> actors = new ArrayList<>();
        int hunterCount = 0;

        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Organism organism = maybeCreateOrganism(field, location, hunterCount);
                if(organism != null) {
                    actors.add((Actor) organism);
                    if(organism instanceof Hunter) {
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
                grassActors.add(createOffspring(Grass.class, field, location));
            }
        }

        return grassActors;
    }

    public <T extends Organism> T createInitialOrganism(Class<T> organismClass, Field field, Location location)
    {
        Animal.Gender sex = getRandomSex();
        return create(organismClass, field, location, new OrganismCreationOptions(true, sex));
    }

    public <T extends Organism> T createOffspring(Class<T> organismClass, Field field, Location location)
    {
        return create(organismClass, field, location, new OrganismCreationOptions(false, null));
    }

    public <T extends Organism> T createOffspring(Class<T> organismClass,
            Field field,
            Location location,
            Animal.Gender sex)
    {
        return create(organismClass, field, location, new OrganismCreationOptions(false, sex));
    }

    private Organism maybeCreateOrganism(Field field, Location location, int hunterCount)
    {
        Random rand = randomProvider.getRandom();
        for(Class<? extends Organism> organismClass : CREATION_ORDER) {
            if(organismClass == Hunter.class && hunterCount >= config.getHunterLimit()) {
                continue;
            }

            if(rand.nextDouble() <= config.getCreationProbability(organismClass)) {
                return createInitialOrganism(organismClass, field, location);
            }
        }

        return null;
    }

    private <T extends Organism> T create(Class<T> organismClass,
            Field field,
            Location location,
            OrganismCreationOptions options)
    {
        OrganismCreator<? extends Organism> creator = creators.get(organismClass);
        if(creator == null) {
            throw new IllegalArgumentException("Unsupported organism type: " + organismClass.getName());
        }

        T organism = organismClass.cast(creator.create(field, location, options));
        diseaseService.initializeOrganism(organism);
        return organism;
    }

    private Animal.Gender getRandomSex()
    {
        return randomProvider.getRandom().nextBoolean()
                ? Animal.Gender.MALE
                : Animal.Gender.FEMALE;
    }

    private static Animal.Gender requireSex(OrganismCreationOptions options)
    {
        if(options.sex() == null) {
            throw new IllegalArgumentException("Animal creation requires a sex.");
        }
        return options.sex();
    }

    private record OrganismCreationOptions(boolean randomAge, Animal.Gender sex) { }

    @FunctionalInterface
    private interface OrganismCreator<T extends Organism>
    {
        T create(Field field, Location location, OrganismCreationOptions options);
    }
}
