package safari;

/**
 * The logical kinds of actors that can appear in the simulation.
 */
public enum ActorKind
{
    GAZELLE("Gazelle"),
    ZEBRA("Zebra"),
    CHEETAH("Cheetah"),
    LION("Lion"),
    JAGUAR("Jaguar"),
    GRASS("Grass"),
    HUNTER("Hunter");

    private final String displayName;

    ActorKind(String displayName)
    {
        this.displayName = displayName;
    }

    public String displayName()
    {
        return displayName;
    }
}
