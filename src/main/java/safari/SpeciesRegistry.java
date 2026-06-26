package safari;

import java.util.EnumMap;
import java.util.Properties;
import java.util.Map;

/**
 * Central registry for immutable species configuration and construction.
 */
final class SpeciesRegistry
{
    private static final Map<SpeciesType, SpeciesConfig> CONFIGS = loadConfigs();

    private SpeciesRegistry()
    {
    }

    static SpeciesConfig config(SpeciesType type)
    {
        SpeciesConfig config = CONFIGS.get(type);
        if(config == null) {
            throw new IllegalArgumentException("Unknown species type: " + type);
        }
        return config;
    }

    static Animal create(SpeciesType type, boolean randomAge, Field field, Location location)
    {
        return new Animal(type, randomAge, field, location);
    }

    private static Map<SpeciesType, SpeciesConfig> loadConfigs()
    {
        Properties properties = ConfigLoader.loadProperties("config/species.properties");
        Map<SpeciesType, SpeciesConfig> configs = new EnumMap<>(SpeciesType.class);
        for(SpeciesType type : SpeciesType.values()) {
            String prefix = "species." + type.name() + ".";
            configs.put(
                type,
                new SpeciesConfig(
                    type,
                    ConfigLoader.requiredInt(properties, prefix + "breedingAge"),
                    ConfigLoader.requiredInt(properties, prefix + "maxAge"),
                    ConfigLoader.requiredDouble(properties, prefix + "breedingProbability"),
                    ConfigLoader.requiredInt(properties, prefix + "maxLitterSize"),
                    ConfigLoader.requiredInt(properties, prefix + "maxTimeUntilBreedingAgain"),
                    ConfigLoader.requiredInt(properties, prefix + "initialFoodLevel"),
                    ConfigLoader.requiredInt(properties, prefix + "randomFoodUpperBound"),
                    ConfigLoader.requiredDouble(properties, prefix + "maxFoodLevel"),
                    ConfigLoader.requiredDouble(properties, prefix + "initialGrowthScale"),
                    ConfigLoader.requiredDouble(properties, prefix + "actGrowthIncrement"),
                    ConfigLoader.requiredWeatherMap(properties, prefix + "foodFinding."),
                    ConfigLoader.requiredActorKindMap(properties, prefix + "food.")
                )
            );
        }
        return configs;
    }
}
