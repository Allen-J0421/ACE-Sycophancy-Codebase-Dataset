/**
 * Strategy controlling how an organism chooses its next location.
 *
 * @version 2022.03.02
 */
public interface RelocationStrategy
{
    Location findNextLocation(Organism organism);

    boolean diesWhenBlocked();
}
