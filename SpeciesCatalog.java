import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the species definitions used by the simulator.
 */
public class SpeciesCatalog {

    private static final double ANIMAL_CREATION_PROBABILITY = 0.05;
    private static final double PLANT_CREATION_PROBABILITY = 0.04;

    private final List<SpeciesDefinition> definitions;
    private final Map<Class<? extends Organism>, SpeciesDefinition> definitionsByType;

    public SpeciesCatalog() {
        definitions = Arrays.asList(
                new SpeciesDefinition(Lion.class, Color.RED, ANIMAL_CREATION_PROBABILITY, Lion::new),
                new SpeciesDefinition(Zebra.class, Color.BLUE, ANIMAL_CREATION_PROBABILITY, Zebra::new),
                new SpeciesDefinition(Vulture.class, Color.ORANGE, ANIMAL_CREATION_PROBABILITY, Vulture::new),
                new SpeciesDefinition(Grass.class, Color.GREEN, PLANT_CREATION_PROBABILITY, Grass::new),
                new SpeciesDefinition(Goat.class, Color.PINK, ANIMAL_CREATION_PROBABILITY, Goat::new),
                new SpeciesDefinition(Elephant.class, Color.GRAY, ANIMAL_CREATION_PROBABILITY, Elephant::new),
                new SpeciesDefinition(Cheetah.class, Color.MAGENTA, ANIMAL_CREATION_PROBABILITY, Cheetah::new),
                new SpeciesDefinition(PoisonBerry.class, Color.BLACK, PLANT_CREATION_PROBABILITY, PoisonBerry::new)
        );
        definitionsByType = new LinkedHashMap<>();
        for (SpeciesDefinition definition : definitions) {
            definitionsByType.put(definition.getOrganismType(), definition);
        }
    }

    public List<SpeciesDefinition> getDefinitions() {
        return definitions;
    }

    public SpeciesDefinition findDefinition(Class<? extends Organism> organismType) {
        return definitionsByType.get(organismType);
    }

    public String getDisplayName(Class<? extends Organism> organismType) {
        SpeciesDefinition definition = findDefinition(organismType);
        return definition != null ? definition.getDisplayName() : organismType.getSimpleName();
    }

    public Color getColor(Class<? extends Organism> organismType) {
        SpeciesDefinition definition = findDefinition(organismType);
        return definition != null ? definition.getColor() : null;
    }
}
