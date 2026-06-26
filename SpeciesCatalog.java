import java.awt.Color;

/**
 * Central registry of simulator species metadata.
 */
public final class SpeciesCatalog
{
    private static final SpeciesDefinition[] DEFINITIONS = new SpeciesDefinition[]{
        new SpeciesDefinition(Salmon.class, 0.08, Color.YELLOW,
            (field, location) -> new Salmon(true, field, location)),
        new SpeciesDefinition(Cod.class, 0.08, Color.ORANGE,
            (field, location) -> new Cod(true, field, location)),
        new SpeciesDefinition(Seaweed.class, 0.03, Color.RED,
            (field, location) -> new Seaweed(true, field, location)),
        new SpeciesDefinition(Shark.class, 0.04, Color.BLACK,
            (field, location) -> new Shark(true, field, location)),
        new SpeciesDefinition(Whale.class, 0.03, Color.PINK,
            (field, location) -> new Whale(true, field, location))
    };

    private SpeciesCatalog()
    {
    }

    public static SpeciesDefinition[] getDefinitions()
    {
        return DEFINITIONS.clone();
    }
}
