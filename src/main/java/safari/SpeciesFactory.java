package safari;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central registry for species configuration and construction.
 */
final class SpeciesFactory
{
    private static final Map<SpeciesType, SpeciesConfig> CONFIGS = new EnumMap<>(SpeciesType.class);

    static {
        CONFIGS.put(
            SpeciesType.GAZELLE,
            new SpeciesConfig(
                SpeciesType.GAZELLE,
                2,
                80,
                0.8900995,
                7,
                2,
                14,
                14,
                28,
                75.0,
                0.012,
                Map.of(
                    Weather.SUNNY, 1.0,
                    Weather.RAINY, 1.0,
                    Weather.FOGGY, 1.0
                ),
                Map.of(Grass.class, 14)
            )
        );
        CONFIGS.put(
            SpeciesType.ZEBRA,
            new SpeciesConfig(
                SpeciesType.ZEBRA,
                2,
                160,
                0.27587999058995,
                4,
                15,
                19,
                19,
                28,
                67.0,
                0.015,
                Map.of(
                    Weather.SUNNY, 1.0,
                    Weather.RAINY, 1.0,
                    Weather.FOGGY, 1.0
                ),
                Map.of(Grass.class, 19)
            )
        );
        CONFIGS.put(
            SpeciesType.CHEETAH,
            new SpeciesConfig(
                SpeciesType.CHEETAH,
                2,
                1200,
                0.4196975694969952,
                4,
                11,
                34,
                34,
                40,
                102.0,
                0.012,
                Map.of(
                    Weather.SUNNY, 0.9,
                    Weather.RAINY, 0.8,
                    Weather.FOGGY, 0.7
                ),
                Map.of(
                    Zebra.class, 34,
                    Gazelle.class, 33
                )
            )
        );
        CONFIGS.put(
            SpeciesType.LION,
            new SpeciesConfig(
                SpeciesType.LION,
                15,
                1500,
                0.40752995,
                2,
                20,
                25,
                25,
                50,
                100.0,
                0.01,
                Map.of(
                    Weather.SUNNY, 1.0,
                    Weather.RAINY, 0.9,
                    Weather.FOGGY, 0.8
                ),
                Map.of(
                    Gazelle.class, 24,
                    Cheetah.class, 25
                )
            )
        );
        CONFIGS.put(
            SpeciesType.JAGUAR,
            new SpeciesConfig(
                SpeciesType.JAGUAR,
                10,
                1000,
                0.2,
                5,
                6,
                35,
                35,
                39,
                89.0,
                0.013,
                Map.of(
                    Weather.SUNNY, 1.0,
                    Weather.RAINY, 0.7,
                    Weather.FOGGY, 0.4
                ),
                Map.of(Gazelle.class, 35)
            )
        );
    }

    private SpeciesFactory()
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
        return switch(type) {
            case GAZELLE -> new Gazelle(randomAge, field, location);
            case ZEBRA -> new Zebra(randomAge, field, location);
            case CHEETAH -> new Cheetah(randomAge, field, location);
            case LION -> new Lion(randomAge, field, location);
            case JAGUAR -> new Jaguar(randomAge, field, location);
        };
    }
}
