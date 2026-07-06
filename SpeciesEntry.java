import java.awt.Color;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Immutable configuration bundle for one species used during field initialization.
 * Groups together the species runtime class (for color registration), the display
 * color, the per-cell creation probability, and the factory that instantiates
 * organisms of that species.
 *
 * @version 2022.03.02
 */
public class SpeciesEntry {

    /** Runtime class of the species — used to register its display color. */
    public final Class speciesClass;
    /** Color used to paint cells occupied by this species. */
    public final Color displayColor;
    /** Probability that a cell in the initial grid contains this species. */
    public final double creationProbability;
    /** Factory that creates one organism of this species at a given location. */
    public final EntityFactory factory;

    /**
     * Construct a configuration entry for one species.
     *
     * @param speciesClass        Runtime class of the species.
     * @param displayColor        Color to use when painting this species.
     * @param creationProbability Per-cell creation probability (0.0–1.0).
     * @param factory             Factory used to instantiate organisms.
     */
    public SpeciesEntry(Class speciesClass, Color displayColor,
                        double creationProbability, EntityFactory factory) {
        this.speciesClass        = speciesClass;
        this.displayColor        = displayColor;
        this.creationProbability = creationProbability;
        this.factory             = factory;
    }
}
