package savannah.model;

import java.awt.Color;
import java.util.Random;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

/**
 * Enumerates the supported species and their behavior/attribute mappings.
 */
public enum SpeciesType
{
    LION("Lion", Color.RED, Color.WHITE)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Lion(context, randomAge, location, infected, immune);
        }
    },
    CHEETAH("Cheetah", Color.ORANGE, Color.BLACK)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Cheetah(context, randomAge, location, infected, immune);
        }
    },
    ZEBRA("Zebra", Color.BLACK, Color.WHITE)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Zebra(context, randomAge, location, infected, immune);
        }
    },
    GIRAFFE("Giraffe", Color.YELLOW, Color.BLACK)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Giraffe(context, randomAge, location, infected, immune);
        }
    },
    LEMUR("Lemur", Color.BLUE, Color.WHITE)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Lemur(context, randomAge, location, infected, immune);
        }
    },
    PLANT("Plant", Color.GREEN, Color.BLACK)
    {
        @Override
        public Plant createPlant(SimulationContext context, boolean randomHealthPercentage, Location location)
        {
            return new Plant(context, randomHealthPercentage, location);
        }
    };

    private final String displayName;
    private final Color fillColor;
    private final Color textColor;

    SpeciesType(String displayName, Color fillColor, Color textColor)
    {
        this.displayName = displayName;
        this.fillColor = fillColor;
        this.textColor = textColor;
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

    public boolean isPrey()
    {
        return isAnimal() && !isPredator();
    }

    public Color getFillColor()
    {
        return fillColor;
    }

    public Color getTextColor()
    {
        return textColor;
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

    public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
    {
        throw new UnsupportedOperationException(displayName + " is not an animal factory");
    }

    public Animal createAnimal(Field field, Location location, boolean randomAge, boolean infected, boolean immune, SimulationConfig config)
    {
        return createAnimal(new SimulationContext(field, config), location, randomAge, infected, immune);
    }

    public Plant createPlant(SimulationContext context, boolean randomHealthPercentage, Location location)
    {
        throw new UnsupportedOperationException(displayName + " is not a plant factory");
    }

    public Plant createPlant(boolean randomHealthPercentage, Field field, Location location, SimulationConfig config)
    {
        return createPlant(new SimulationContext(field, config), randomHealthPercentage, location);
    }
}
