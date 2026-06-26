package savannah.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry for the built-in organism factories.
 */
public final class SpeciesRegistry
{
    public static final SpeciesRegistry INSTANCE = new SpeciesRegistry();

    private final Map<SpeciesType, OrganismFactory> factories;

    private SpeciesRegistry()
    {
        factories = new EnumMap<>(SpeciesType.class);
        for (SpeciesFactory factory : SpeciesFactory.values()) {
            factories.put(factory.getSpeciesType(), factory);
        }
    }

    public OrganismFactory getFactory(SpeciesType speciesType)
    {
        OrganismFactory factory = factories.get(speciesType);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown species type: " + speciesType);
        }
        return factory;
    }
}
