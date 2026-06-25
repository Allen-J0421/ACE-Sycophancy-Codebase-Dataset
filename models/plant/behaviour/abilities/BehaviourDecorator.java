package models.plant.behaviour.abilities;

/**
 * Various behaviours of the plants.
 * Implemented using Decorator design pattern.
 *
 */
public abstract class BehaviourDecorator implements Behaviour {
    private Behaviour behaviour;

    public BehaviourDecorator(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public void act() {
        behaviour.act();
    }
}
