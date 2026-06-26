package savannah.model;

import java.util.Random;

import savannah.config.SimulationConfig;

/**
 * Enumerates the supported species and their behavior/attribute mappings.
 */
public enum SpeciesType
{
    LION("Lion")
    {
        @Override
        public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
        {
            return new Lion(randomAge, field, location, infected, immune, config);
        }
    },
    CHEETAH("Cheetah")
    {
        @Override
        public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
        {
            return new Cheetah(randomAge, field, location, infected, immune, config);
        }
    },
    ZEBRA("Zebra")
    {
        @Override
        public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
        {
            return new Zebra(randomAge, field, location, infected, immune, config);
        }
    },
    GIRAFFE("Giraffe")
    {
        @Override
        public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
        {
            return new Giraffe(randomAge, field, location, infected, immune, config);
        }
    },
    LEMUR("Lemur")
    {
        @Override
        public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
        {
            return new Lemur(randomAge, field, location, infected, immune, config);
        }
    },
    PLANT("Plant")
    {
        @Override
        public Plant createPlant(boolean randomHealthPercentage, Field field, Location location, SimulationConfig config)
        {
            return new Plant(randomHealthPercentage, field, location, config);
        }
    };

    private final String displayName;

    SpeciesType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public boolean isPlant()
    {
        return this == PLANT;
    }

    public boolean isAnimal()
    {
        return !isPlant();
    }

    public boolean isPredator()
    {
        return this == LION || this == CHEETAH;
    }

    public SimulationConfig.SpeciesConfig animalConfig(SimulationConfig config)
    {
        return switch (this) {
            case LION -> config.lion;
            case CHEETAH -> config.cheetah;
            case ZEBRA -> config.zebra;
            case GIRAFFE -> config.giraffe;
            case LEMUR -> config.lemur;
            case PLANT -> throw new IllegalStateException("Plant does not use animal config");
        };
    }

    public SimulationConfig.PlantConfig plantConfig(SimulationConfig config)
    {
        if (!isPlant()) {
            throw new IllegalStateException("Only plant species use plant config");
        }
        return config.plant;
    }

    public int initialAge(boolean randomAge, Random rand, SimulationConfig config)
    {
        if (!isAnimal()) {
            throw new IllegalStateException("Only animal species have ages");
        }

        return randomAge ? rand.nextInt(animalConfig(config).maxAge) : 0;
    }

    public int initialFoodLevel(boolean randomAge, Random rand, SimulationConfig config)
    {
        if (!isAnimal()) {
            throw new IllegalStateException("Only animal species have food levels");
        }

        SimulationConfig.SpeciesConfig speciesConfig = animalConfig(config);
        if (randomAge) {
            return rand.nextInt(speciesConfig.maxFoodLevel);
        }
        if (speciesConfig.useRandomNewbornFoodLevel) {
            double percentageOfMaxFoodLevel = rand.nextDouble() * speciesConfig.randomNewbornFoodLevelMaxFraction;
            return (int) (percentageOfMaxFoodLevel * speciesConfig.maxFoodLevel);
        }
        return (int) (speciesConfig.newbornFoodLevelFraction * speciesConfig.maxFoodLevel);
    }

    public double initialPlantHealth(boolean randomHealth, Random rand, SimulationConfig config)
    {
        if (!isPlant()) {
            throw new IllegalStateException("Only plant species have health");
        }

        SimulationConfig.PlantConfig plantConfig = plantConfig(config);
        if (!randomHealth) {
            return plantConfig.newbornHealthFraction;
        }

        double healthPercentage = rand.nextDouble();
        return Math.max(healthPercentage, plantConfig.minimumRandomHealthFraction);
    }

    public double getPlantSpreadProbability(Weather.WeatherType weather, SimulationConfig config)
    {
        if (!isPlant()) {
            throw new IllegalStateException("Only plant species have spread probabilities");
        }

        return switch (weather) {
            case Sunny -> config.sunnyPlantSpreadProbability;
            case Rainy -> config.rainyPlantSpreadProbability;
            case Foggy -> config.foggyPlantSpreadProbability;
            case Cloudy -> config.cloudyPlantSpreadProbability;
            case Clear -> config.clearPlantSpreadProbability;
        };
    }

    public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
    {
        throw new UnsupportedOperationException(displayName + " is not an animal factory");
    }

    public Plant createPlant(boolean randomHealthPercentage, Field field, Location location, SimulationConfig config)
    {
        throw new UnsupportedOperationException(displayName + " is not a plant factory");
    }
}
