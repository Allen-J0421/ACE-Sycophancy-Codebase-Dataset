package safari;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Central registry for population seeding rules.
 */
final class SpawnRegistry
{
    private static final List<SpawnRule> RULES = loadRules();

    private SpawnRegistry()
    {
    }

    static Actor seedActor(Field field, int row, int col, Random random)
    {
        Location location = new Location(row, col);
        for(SpawnRule rule : RULES) {
            if(random.nextDouble() <= rule.probability()) {
                return createActor(rule.kind(), field, location);
            }
        }
        return null;
    }

    private static List<SpawnRule> loadRules()
    {
        Properties properties = ConfigLoader.loadProperties("config/spawn.properties");
        String orderValue = ConfigLoader.requiredString(properties, "spawn.order");
        return Arrays.stream(orderValue.split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .map(value -> new SpawnRule(
                ActorKind.valueOf(value),
                ConfigLoader.requiredDouble(properties, "spawn." + value)
            ))
            .toList();
    }

    private static Actor createActor(ActorKind kind, Field field, Location location)
    {
        return switch(kind) {
            case GAZELLE -> SpeciesRegistry.create(SpeciesType.GAZELLE, true, field, location);
            case ZEBRA -> SpeciesRegistry.create(SpeciesType.ZEBRA, true, field, location);
            case CHEETAH -> SpeciesRegistry.create(SpeciesType.CHEETAH, true, field, location);
            case LION -> SpeciesRegistry.create(SpeciesType.LION, true, field, location);
            case JAGUAR -> SpeciesRegistry.create(SpeciesType.JAGUAR, true, field, location);
            case GRASS -> new Grass(true, field, location);
            case HUNTER -> new Hunter(field, location);
        };
    }
}
