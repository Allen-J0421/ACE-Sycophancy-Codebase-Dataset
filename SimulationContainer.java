import java.util.EnumMap;
import java.util.Set;

/**
 * Dependency container for simulation services, configuration, and factories.
 *
 * @version 2022.03.02
 */
public class SimulationContainer
{
    private static final String DEFAULT_CONFIG_FILE = "organism-config.properties";

    private final SpeciesConfigurationRepository configurationRepository;
    private final Weather weather;
    private final SimulationRulesEngine rulesEngine;
    private final EnumMap<Species, OrganismAttributes> attributesBySpecies;
    private final PopulationRule[] primaryPopulationRules;
    private final PopulationRule[] secondaryPopulationRules;

    public SimulationContainer()
    {
        this(new PropertiesSpeciesConfigurationRepository(DEFAULT_CONFIG_FILE),
             new Weather());
    }

    public SimulationContainer(SpeciesConfigurationRepository configurationRepository,
                               Weather weather)
    {
        this.configurationRepository = configurationRepository;
        this.weather = weather;
        rulesEngine = new SimulationRulesEngine(weather);
        attributesBySpecies = buildAttributes();
        primaryPopulationRules = buildPrimaryPopulationRules();
        secondaryPopulationRules = buildSecondaryPopulationRules();
    }

    public SimulationRulesEngine getRulesEngine()
    {
        return rulesEngine;
    }

    public SimulationDisplay createDisplay(int depth, int width, boolean headless)
    {
        if(headless) {
            return new HeadlessSimulationView(rulesEngine);
        }
        return new SimulatorView(depth, width, rulesEngine);
    }

    public PopulationRule[] getPrimaryPopulationRules()
    {
        return primaryPopulationRules;
    }

    public PopulationRule[] getSecondaryPopulationRules()
    {
        return secondaryPopulationRules;
    }

    private EnumMap<Species, OrganismAttributes> buildAttributes()
    {
        EnumMap<Species, OrganismAttributes> attributes =
            new EnumMap<>(Species.class);
        attributes.put(Species.BEAR,
                       createAnimalAttributes(Species.BEAR,
                                              organismFactory(Species.BEAR),
                                              OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                              OrganismBehaviors.FORAGE_OR_WANDER,
                                              OrganismBehaviors.INCREMENT_AGE,
                                              OrganismBehaviors.DECAY_HEALTH,
                                              OrganismBehaviors.APPLY_ILLNESS,
                                              OrganismBehaviors.SPREAD_DISEASE,
                                              OrganismBehaviors.GIVE_BIRTH,
                                              OrganismBehaviors.RELOCATE));
        attributes.put(Species.HIPPOPOTAMUS,
                       createAnimalAttributes(Species.HIPPOPOTAMUS,
                                              organismFactory(Species.HIPPOPOTAMUS),
                                              OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                              OrganismBehaviors.FORAGE_OR_WANDER,
                                              OrganismBehaviors.INCREMENT_AGE,
                                              OrganismBehaviors.DECAY_HEALTH,
                                              OrganismBehaviors.GIVE_BIRTH,
                                              OrganismBehaviors.RELOCATE));
        attributes.put(Species.LEOPARD,
                       createAnimalAttributes(Species.LEOPARD,
                                              organismFactory(Species.LEOPARD),
                                              OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                              OrganismBehaviors.FORAGE_OR_WANDER,
                                              OrganismBehaviors.INCREMENT_AGE,
                                              OrganismBehaviors.DECAY_HEALTH,
                                              OrganismBehaviors.GIVE_BIRTH,
                                              OrganismBehaviors.RELOCATE));
        attributes.put(Species.MONKEY,
                       createAnimalAttributes(Species.MONKEY,
                                              organismFactory(Species.MONKEY),
                                              OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                              OrganismBehaviors.FORAGE_OR_WANDER,
                                              OrganismBehaviors.INCREMENT_AGE,
                                              OrganismBehaviors.DECAY_HEALTH,
                                              OrganismBehaviors.GIVE_BIRTH,
                                              OrganismBehaviors.RELOCATE));
        attributes.put(Species.SLOTH,
                       createAnimalAttributes(Species.SLOTH,
                                              organismFactory(Species.SLOTH),
                                              OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                              OrganismBehaviors.FORAGE_OR_WANDER,
                                              OrganismBehaviors.INCREMENT_AGE,
                                              OrganismBehaviors.DECAY_HEALTH,
                                              OrganismBehaviors.APPLY_ILLNESS,
                                              OrganismBehaviors.SPREAD_DISEASE,
                                              OrganismBehaviors.GIVE_BIRTH,
                                              OrganismBehaviors.RELOCATE));
        attributes.put(Species.PLANT,
                       createOrganismAttributes(Species.PLANT,
                                                organismFactory(Species.PLANT),
                                                OrganismBehaviors.AGE_BASED_BREEDING,
                                                OrganismBehaviors.STATIONARY,
                                                OrganismBehaviors.INCREMENT_AGE,
                                                OrganismBehaviors.GIVE_BIRTH,
                                                OrganismBehaviors.RELOCATE));
        return attributes;
    }

    private OrganismFactory organismFactory(final Species species)
    {
        return new OrganismFactory() {
            public Organism create(boolean randomAge, Field field, Location location)
            {
                switch(species) {
                    case BEAR:
                        return new Bear(randomAge, field, location,
                                        getAnimalAttributes(species), weather);
                    case HIPPOPOTAMUS:
                        return new Hippopotamus(randomAge, field, location,
                                                getAnimalAttributes(species), weather);
                    case LEOPARD:
                        return new Leopard(randomAge, field, location,
                                           getAnimalAttributes(species), weather);
                    case MONKEY:
                        return new Monkey(randomAge, field, location,
                                          getAnimalAttributes(species), weather);
                    case SLOTH:
                        return new Sloth(randomAge, field, location,
                                         getAnimalAttributes(species), weather);
                    case PLANT:
                        return new Plant(randomAge, field, location,
                                         getOrganismAttributes(species));
                    default:
                        throw new IllegalStateException("Unsupported species " + species);
                }
            }
        };
    }

    private PopulationRule[] buildPrimaryPopulationRules()
    {
        return new PopulationRule[] {
            populationRule(Species.HIPPOPOTAMUS, DisplayColor.DARK_GRAY),
            populationRule(Species.LEOPARD, DisplayColor.MAGENTA),
            populationRule(Species.BEAR, DisplayColor.RED),
            populationRule(Species.MONKEY, DisplayColor.ORANGE)
        };
    }

    private PopulationRule[] buildSecondaryPopulationRules()
    {
        return new PopulationRule[] {
            populationRule(Species.SLOTH, DisplayColor.YELLOW),
            populationRule(Species.PLANT, DisplayColor.GREEN)
        };
    }

    private PopulationRule populationRule(Species species, DisplayColor color)
    {
        return new PopulationRule(configurationRepository.getConfig(species)
                                      .getCreationProbability(),
                                  organismFactory(species),
                                  species, color);
    }

    private OrganismAttributes createOrganismAttributes(Species species,
                                                        OrganismFactory factory,
                                                        BreedingStrategy breedingStrategy,
                                                        RelocationStrategy relocationStrategy,
                                                        OrganismActionCommand... actionCommands)
    {
        SpeciesConfig config = configurationRepository.getConfig(species);
        return new OrganismAttributes(species, config.isDiurnal(),
                                      config.getBreedingAge(), config.getMaxAge(),
                                      config.getBreedingProbability(),
                                      config.getMaxLitterSize(), factory,
                                      breedingStrategy, relocationStrategy,
                                      actionCommands);
    }

    private AnimalAttributes createAnimalAttributes(Species species,
                                                    OrganismFactory factory,
                                                    BreedingStrategy breedingStrategy,
                                                    RelocationStrategy relocationStrategy,
                                                    OrganismActionCommand... actionCommands)
    {
        SpeciesConfig config = configurationRepository.getConfig(species);
        Integer maxHealth = config.getMaxHealth();
        if(maxHealth == null) {
            throw new IllegalStateException(
                "Missing maxHealth for animal species " + species);
        }

        Set<Species> foodSources = config.getFoodSources();
        return new AnimalAttributes(species, config.isDiurnal(),
                                    config.getBreedingAge(), config.getMaxAge(),
                                    config.getBreedingProbability(),
                                    config.getMaxLitterSize(), maxHealth.intValue(),
                                    factory, foodSources, breedingStrategy,
                                    relocationStrategy, actionCommands);
    }

    private AnimalAttributes getAnimalAttributes(Species species)
    {
        return (AnimalAttributes) attributesBySpecies.get(species);
    }

    private OrganismAttributes getOrganismAttributes(Species species)
    {
        return attributesBySpecies.get(species);
    }
}
