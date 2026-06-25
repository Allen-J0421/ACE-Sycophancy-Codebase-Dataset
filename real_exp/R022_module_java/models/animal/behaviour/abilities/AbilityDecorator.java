package models.animal.behaviour.abilities;

/**
 * This class allows abilities to be chained together.
 *
 */
public abstract class AbilityDecorator implements Ability {
    private Ability ability;

    /**
     * Constructor for AbilityDecorator.
     * @param ability The base ability to be chained.
     */
    public AbilityDecorator(Ability ability) {
        this.ability = ability;
    }

    /**
     * Enact the ability.
     */
    @Override
    public void act() {
        ability.act();
    }
}
