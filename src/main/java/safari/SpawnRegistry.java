package safari;

import java.util.List;
import java.util.Random;

/**
 * Central registry for population seeding rules.
 */
final class SpawnRegistry
{
    private static final List<SpawnRule> RULES = List.of(
        new SpawnRule(0.017, (field, location) -> SpeciesRegistry.create(SpeciesType.LION, true, field, location)),
        new SpawnRule(0.032, (field, location) -> SpeciesRegistry.create(SpeciesType.CHEETAH, true, field, location)),
        new SpawnRule(0.2, (field, location) -> SpeciesRegistry.create(SpeciesType.GAZELLE, true, field, location)),
        new SpawnRule(0.011, (field, location) -> SpeciesRegistry.create(SpeciesType.JAGUAR, true, field, location)),
        new SpawnRule(0.55, (field, location) -> new Grass(true, field, location)),
        new SpawnRule(0.4998, (field, location) -> SpeciesRegistry.create(SpeciesType.ZEBRA, true, field, location)),
        new SpawnRule(0.01, (field, location) -> new Hunter(field, location))
    );

    private SpawnRegistry()
    {
    }

    static Actor seedActor(Field field, int row, int col, Random random)
    {
        Location location = new Location(row, col);
        for(SpawnRule rule : RULES) {
            if(random.nextDouble() <= rule.probability()) {
                return rule.create(field, location);
            }
        }
        return null;
    }
}
