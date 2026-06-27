/**
 * Entry point and composition root. Registers each species provider with
 * EntityFactory in the correct order, then wires the view to the simulator.
 */
public class Main {
    public static void main(String[] args) {
        EntityFactory.register(Dingo.PROVIDER);
        EntityFactory.register(Ant.PROVIDER);
        EntityFactory.register(Snake.PROVIDER);
        EntityFactory.register(Rat.PROVIDER);
        EntityFactory.register(Eagle.PROVIDER);
        EntityFactory.register(Emu.PROVIDER);
        EntityFactory.register(Acacia.PROVIDER);
        EntityFactory.register(Grass.PROVIDER);

        int depth = 80;
        int width = 120;
        EventBus bus = new EventBus();
        SimulatorView view = new SimulatorView(depth, width);
        view.subscribe(bus);
        Simulator sim = new Simulator(depth, width, bus);
        sim.runLongSimulation();
    }
}
