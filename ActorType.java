/**
 * Public descriptor for actor metadata used by UI and statistics code.
 */
public interface ActorType
{
    String getDisplayName();

    ActorCategory getCategory();
}
