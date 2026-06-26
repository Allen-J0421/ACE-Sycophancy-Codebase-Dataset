import java.awt.Color;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;

/**
 * Builds and seeds the simulation runtime.
 *
 * @version 26/02/2022
 */
public class SimulationFactory
{
    public static final int DEFAULT_WIDTH = 270;
    public static final int DEFAULT_DEPTH = 180;

    private static final double LION_CREATION_PROBABILITY = 0.0125;
    private static final double CHEETAH_CREATION_PROBABILITY = 0.0125;
    private static final double ZEBRA_CREATION_PROBABILITY = 0.08;
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.08;
    private static final double LEMUR_CREATION_PROBABILITY = 0.081;

    /**
     * Create the core simulation components.
     */
    public SimulationComponents createSimulation(int depth, int width,
                                                 Runnable playPauseAction,
                                                 Runnable runLongSimulationAction,
                                                 Runnable simulateOneStepAction,
                                                 Runnable resetAction,
                                                 Runnable quitAction)
    {
        Dimensions dimensions = normalizeDimensions(depth, width);
        Time time = new Time();
        Weather weather = new Weather(time);
        Field field = new Field(dimensions.getDepth(), dimensions.getWidth(), time, weather);

        SimulationButtons buttons = createButtons(playPauseAction, runLongSimulationAction,
                                                  simulateOneStepAction, resetAction, quitAction);

        SimulatorView view = new SimulatorView(dimensions.getDepth(), dimensions.getWidth(),
                                               buttons.asArray());
        configureViewColors(view);

        return new SimulationComponents(field, time, weather, view, buttons);
    }

    /**
     * Populate a field with its initial set of plants and animals.
     */
    public void populate(Field field, List<LivingOrganism> animals, List<LivingOrganism> plants)
    {
        Random rand = Randomizer.getRandom();
        field.clear();

        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                populateLocation(field, rand, row, col, animals, plants);
            }
        }
    }

    /**
     * Ensure simulation dimensions are usable, falling back to defaults if not.
     */
    private Dimensions normalizeDimensions(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return new Dimensions(DEFAULT_DEPTH, DEFAULT_WIDTH);
        }

        return new Dimensions(depth, width);
    }

    /**
     * Build the control buttons used by the simulation view.
     */
    private SimulationButtons createButtons(Runnable playPauseAction,
                                            Runnable runLongSimulationAction,
                                            Runnable simulateOneStepAction,
                                            Runnable resetAction,
                                            Runnable quitAction)
    {
        JButton playPauseButton = createButton("Play", playPauseAction, true);
        JButton runLongSimulationButton = createButton("Run Long Sim", runLongSimulationAction, true);
        JButton simulateOneStepButton = createButton("Sim One Step", simulateOneStepAction, false);
        JButton resetButton = createButton("Reset", resetAction, false);
        JButton quitButton = createButton("Quit", quitAction, false);

        return new SimulationButtons(playPauseButton, runLongSimulationButton,
                                     simulateOneStepButton, resetButton, quitButton);
    }

    /**
     * Create a button with the supplied action.
     */
    private JButton createButton(String label, Runnable action, boolean runInBackground)
    {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            if (runInBackground)
            {
                new Thread(action).start();
            }
            else
            {
                action.run();
            }
        });

        return button;
    }

    /**
     * Register the colors used by each simulation class.
     */
    private void configureViewColors(SimulatorView view)
    {
        view.setColor(Zebra.class, Color.BLACK, Color.WHITE);
        view.setColor(Giraffe.class, Color.YELLOW, Color.BLACK);
        view.setColor(Lemur.class, Color.BLUE, Color.WHITE);
        view.setColor(Lion.class, Color.RED, Color.WHITE);
        view.setColor(Cheetah.class, Color.ORANGE, Color.BLACK);
        view.setColor(Plant.class, Color.GREEN, Color.BLACK);
    }

    /**
     * Populate one field location with its starting plant and optional animal.
     */
    private void populateLocation(Field field, Random rand, int row, int col,
                                  List<LivingOrganism> animals, List<LivingOrganism> plants)
    {
        Location location = new Location(row, col);

        plants.add(new Plant(true, field, location));

        Animal animal = createRandomAnimal(field, rand, location);
        if(animal != null) 
        {
            animals.add(animal);
        }
    }

    /**
     * Randomly create an initial animal for a location.
     */
    private Animal createRandomAnimal(Field field, Random rand, Location location)
    {
        if(rand.nextDouble() <= LION_CREATION_PROBABILITY) 
        {
            return new Lion(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY)
        {
            return new Cheetah(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) 
        {
            return new Zebra(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY)
        {
            return new Giraffe(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= LEMUR_CREATION_PROBABILITY) 
        {
            return new Lemur(true, field, location, false, false);
        }

        return null;
    }

    /**
     * Value object for simulation dimensions.
     */
    private static class Dimensions
    {
        private final int depth;
        private final int width;

        private Dimensions(int depth, int width)
        {
            this.depth = depth;
            this.width = width;
        }

        private int getDepth()
        {
            return depth;
        }

        private int getWidth()
        {
            return width;
        }
    }

    /**
     * The complete set of controls used by the simulator UI.
     */
    public static class SimulationButtons
    {
        private final JButton playPauseButton;
        private final JButton runLongSimulationButton;
        private final JButton simulateOneStepButton;
        private final JButton resetButton;
        private final JButton quitButton;

        private SimulationButtons(JButton playPauseButton, JButton runLongSimulationButton,
                                  JButton simulateOneStepButton, JButton resetButton,
                                  JButton quitButton)
        {
            this.playPauseButton = playPauseButton;
            this.runLongSimulationButton = runLongSimulationButton;
            this.simulateOneStepButton = simulateOneStepButton;
            this.resetButton = resetButton;
            this.quitButton = quitButton;
        }

        public JButton getPlayPauseButton()
        {
            return playPauseButton;
        }

        public JButton getRunLongSimulationButton()
        {
            return runLongSimulationButton;
        }

        public JButton getSimulateOneStepButton()
        {
            return simulateOneStepButton;
        }

        public JButton getResetButton()
        {
            return resetButton;
        }

        public JButton getQuitButton()
        {
            return quitButton;
        }

        public JButton[] asArray()
        {
            return new JButton[] {
                playPauseButton,
                runLongSimulationButton,
                simulateOneStepButton,
                resetButton,
                quitButton
            };
        }
    }

    /**
     * The runtime components needed by Simulator to control a simulation.
     */
    public static class SimulationComponents
    {
        private final Field field;
        private final Time time;
        private final Weather weather;
        private final SimulatorView view;
        private final SimulationButtons buttons;

        private SimulationComponents(Field field, Time time, Weather weather,
                                     SimulatorView view, SimulationButtons buttons)
        {
            this.field = field;
            this.time = time;
            this.weather = weather;
            this.view = view;
            this.buttons = buttons;
        }

        public Field getField()
        {
            return field;
        }

        public Time getTime()
        {
            return time;
        }

        public Weather getWeather()
        {
            return weather;
        }

        public SimulatorView getView()
        {
            return view;
        }

        public SimulationButtons getButtons()
        {
            return buttons;
        }
    }
}
