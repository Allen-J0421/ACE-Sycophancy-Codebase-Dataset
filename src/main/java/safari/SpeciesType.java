package safari;

/**
 * Enumerates the animal species handled by the shared species factory.
 */
public enum SpeciesType
{
    GAZELLE,
    ZEBRA,
    CHEETAH,
    LION,
    JAGUAR;

    ActorKind toActorKind()
    {
        return switch(this) {
            case GAZELLE -> ActorKind.GAZELLE;
            case ZEBRA -> ActorKind.ZEBRA;
            case CHEETAH -> ActorKind.CHEETAH;
            case LION -> ActorKind.LION;
            case JAGUAR -> ActorKind.JAGUAR;
        };
    }
}
