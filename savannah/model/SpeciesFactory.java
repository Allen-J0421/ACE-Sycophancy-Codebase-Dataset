package savannah.model;

import java.awt.Color;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

/**
 * Built-in organism factories for the supported species.
 */
public enum SpeciesFactory implements OrganismFactory
{
    LION("Lion", Color.RED, Color.WHITE, true, true, config -> config.lion, null)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Lion(context, randomAge, location, infected, immune);
        }
    },
    CHEETAH("Cheetah", Color.ORANGE, Color.BLACK, true, true, config -> config.cheetah, null)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Cheetah(context, randomAge, location, infected, immune);
        }
    },
    ZEBRA("Zebra", Color.BLACK, Color.WHITE, true, false, config -> config.zebra, null)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Zebra(context, randomAge, location, infected, immune);
        }
    },
    GIRAFFE("Giraffe", Color.YELLOW, Color.BLACK, true, false, config -> config.giraffe, null)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Giraffe(context, randomAge, location, infected, immune);
        }
    },
    LEMUR("Lemur", Color.BLUE, Color.WHITE, true, false, config -> config.lemur, null)
    {
        @Override
        public Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
        {
            return new Lemur(context, randomAge, location, infected, immune);
        }
    },
    PLANT("Plant", Color.GREEN, Color.BLACK, false, false, null, config -> config.plant)
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
    private final boolean plant;
    private final boolean predator;
    private final SpeciesConfigAccessor animalConfigAccessor;
    private final PlantConfigAccessor plantConfigAccessor;

    SpeciesFactory(String displayName, Color fillColor, Color textColor, boolean plant, boolean predator, SpeciesConfigAccessor animalConfigAccessor, PlantConfigAccessor plantConfigAccessor)
    {
        this.displayName = displayName;
        this.fillColor = fillColor;
        this.textColor = textColor;
        this.plant = plant;
        this.predator = predator;
        this.animalConfigAccessor = animalConfigAccessor;
        this.plantConfigAccessor = plantConfigAccessor;
    }

    @Override
    public SpeciesType getSpeciesType()
    {
        return SpeciesType.valueOf(name());
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public Color getFillColor()
    {
        return fillColor;
    }

    @Override
    public Color getTextColor()
    {
        return textColor;
    }

    @Override
    public boolean isPlant()
    {
        return plant;
    }

    @Override
    public boolean isAnimal()
    {
        return !plant;
    }

    @Override
    public boolean isPredator()
    {
        return predator;
    }

    @Override
    public SimulationConfig.SpeciesConfig animalConfig(SimulationConfig config)
    {
        if (animalConfigAccessor == null) {
            throw new IllegalStateException(getDisplayName() + " does not use animal config");
        }
        return animalConfigAccessor.get(config);
    }

    @Override
    public SimulationConfig.PlantConfig plantConfig(SimulationConfig config)
    {
        if (plantConfigAccessor == null) {
            throw new IllegalStateException(getDisplayName() + " does not use plant config");
        }
        return plantConfigAccessor.get(config);
    }

    @FunctionalInterface
    private interface SpeciesConfigAccessor
    {
        SimulationConfig.SpeciesConfig get(SimulationConfig config);
    }

    @FunctionalInterface
    private interface PlantConfigAccessor
    {
        SimulationConfig.PlantConfig get(SimulationConfig config);
    }
}
