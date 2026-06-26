import java.awt.Color;

/**
 * Metadata describing an organism species used by the simulator.
 */
public class SpeciesDefinition {

    private final Class<? extends Organism> organismType;
    private final String displayName;
    private final Color color;
    private final double creationProbability;
    private final OrganismFactory factory;

    public SpeciesDefinition(Class<? extends Organism> organismType, Color color,
                             double creationProbability, OrganismFactory factory) {
        this.organismType = organismType;
        this.displayName = organismType.getSimpleName();
        this.color = color;
        this.creationProbability = creationProbability;
        this.factory = factory;
    }

    public Class<? extends Organism> getOrganismType() {
        return organismType;
    }

    public Color getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCreationProbability() {
        return creationProbability;
    }

    public Organism create(boolean randomAge, Field field, Location location) {
        return factory.create(randomAge, field, location);
    }
}
