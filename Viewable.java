import java.awt.Color;

/**
 * Exposes the display metadata that the simulation view needs from any entity.
 *
 * <p>By implementing this interface, each entity type becomes self-describing
 * from the renderer's perspective: the view no longer maintains an external
 * class-to-color map or hard-codes species names for viability checks.
 *
 * <p>All concrete {@link Entity} subclasses must implement
 * {@link #getDisplayColor()}. {@link #getDisplayName()} has a default in
 * {@code Entity} that returns the simple class name.
 * {@link #countsTowardViability()} is implemented once in {@link Animal}
 * (returns {@code true}) and once in {@link Plant} (returns {@code false}).
 */
public interface Viewable
{
    /**
     * @return the color used to render this entity in the simulation view.
     */
    Color getDisplayColor();

    /**
     * @return a short human-readable name for this entity type (e.g. "Cat").
     */
    String getDisplayName();

    /**
     * @return {@code true} if this entity type should count toward the
     *         viability check (i.e. its extinction would end the simulation).
     *         Animals return {@code true}; plants return {@code false}.
     */
    boolean countsTowardViability();
}
