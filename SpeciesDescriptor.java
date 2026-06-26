import java.awt.Color;

/**
 * Shared metadata for simulation species.
 */
public interface SpeciesDescriptor
{
    /**
     * @return the user-facing species name.
     */
    String getDisplayName();

    /**
     * @return the color used to render the species.
     */
    Color getColor();

    /**
     * @return the probability of the species spawning at a given location.
     */
    double getSpawnProbability();

    /**
     * @return the actor class represented by this descriptor.
     */
    Class<?> getActorClass();
}
