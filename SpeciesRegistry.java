import java.util.HashMap;
import java.util.Map;

/**
 * Central data layer that defines every species' simulation parameters as
 * {@link SpeciesConfig} data objects and loads them by name.
 *
 * Keeping the tuning numbers here - rather than hardcoded inside Lion, Grass,
 * and the other species classes - separates the simulation's configuration
 * data from its behaviour. The species name keys match each class's simple
 * name, and the definitions could just as easily be loaded from an external
 * source (a properties/JSON file, a database) in this one place.
 *
 * @version 2022.03.01
 */
public final class SpeciesRegistry
{
    // Species name (matching the class's simple name) -> its configuration.
    private static final Map<String, SpeciesConfig> CONFIGS = new HashMap<>();

    static {
        register("Lion", SpeciesConfig.builder()
            .breedingAge(15).maxAge(200).breedingProbability(0.5)
            .maxLitterSize(4).foodValue(15).startingFoodLevel(20)
            .huntProbability(0.65).nocturnal(false)
            .prey("Gazelle").build());

        register("Hyena", SpeciesConfig.builder()
            .breedingAge(10).maxAge(150).breedingProbability(0.60)
            .maxLitterSize(2).foodValue(15).startingFoodLevel(15)
            .huntProbability(0.63).nocturnal(true)
            .prey("Fennec Fox", "Gazelle").build());

        register("FennecFox", SpeciesConfig.builder()
            .breedingAge(12).maxAge(100).breedingProbability(0.5)
            .maxLitterSize(2).foodValue(12).startingFoodLevel(12)
            .huntProbability(0.6).nocturnal(true)
            .prey("Grass", "Mouse").build());

        register("Mouse", SpeciesConfig.builder()
            .breedingAge(4).maxAge(40).breedingProbability(0.2)
            .maxLitterSize(4).foodValue(10).startingFoodLevel(10)
            .huntProbability(0.7).nocturnal(false)
            .prey("Grass").build());

        register("Gazelle", SpeciesConfig.builder()
            .breedingAge(10).maxAge(45).breedingProbability(0.5)
            .maxLitterSize(4).foodValue(20).startingFoodLevel(20)
            .nocturnal(false)
            .prey("Grass").build());

        register("Grass", SpeciesConfig.builder()
            .maxAge(20).breedingProbability(0.44).maxLitterSize(6).foodValue(10)
            .build());
    }

    private SpeciesRegistry() { }

    private static void register(String species, SpeciesConfig config)
    {
        CONFIGS.put(species, config);
    }

    /**
     * Load the configuration for the named species.
     * @param species The species name (its class's simple name).
     * @return The species' configuration.
     * @throws IllegalArgumentException if no configuration is registered for it.
     */
    public static SpeciesConfig get(String species)
    {
        SpeciesConfig config = CONFIGS.get(species);
        if (config == null) {
            throw new IllegalArgumentException("No configuration registered for species: " + species);
        }
        return config;
    }
}
