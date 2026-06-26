import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 * A graphical view of the simulation grid.
 *
 * The view coordinates reusable UI components for the field, status display,
 * and simulation controls.
 *
 * @version 2022.03.3
 */
public class SimulatorView extends JFrame implements SimulationListener
{
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final FieldView fieldView;
    private final SimulationStatusPanel statusPanel;
    private final SimulationControlPanel controlPanel;
    private final Map<Class<?>, Color> colors;
    private final FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Prey and predator simulation");
        setLocation(0, 0);

        statusPanel = new SimulationStatusPanel();
        fieldView = new FieldView(height, width);
        controlPanel = new SimulationControlPanel();

        Container contents = getContentPane();
        contents.add(statusPanel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(controlPanel, BorderLayout.EAST);

        for(Class<?> speciesClass : SimulationInfo.ALL_ACTORS) {
            controlPanel.setSpeciesColor(speciesClass, SimulationInfo.DEFAULT_COLOR_MAP.get(speciesClass));
        }

        pack();
        setVisible(false);
    }

    public void addOneStepButtonListener(ActionListener listener)
    {
        controlPanel.addOneStepButtonListener(listener);
    }

    public void addResetButtonListener(ActionListener listener)
    {
        controlPanel.addResetButtonListener(listener);
    }

    public void addStopButtonListener(ActionListener listener)
    {
        controlPanel.addStopButtonListener(listener);
    }

    public void addLongButtonListener(ActionListener listener)
    {
        controlPanel.addLongButtonListener(listener);
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        statusPanel.setInfoText(text);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, String weather, String time, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        statusPanel.updateStatus(step, weather, time);
        stats.reset();
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getClass());
                    if(actor instanceof Organism && ((Organism) actor).isDiseased()) {
                        stats.incrementDiseasedCount();
                    }
                    fieldView.drawMark(col, row, getColor(actor.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();
        for(Class<?> speciesClass : SimulationInfo.ALL_ACTORS) {
            controlPanel.updateSpeciesCount(speciesClass, stats.getCountDetails(speciesClass));
        }
        controlPanel.updateDiseasedPopulation(stats.getDiseasedPopulation());
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    @Override
    public void stepCompleted(SimulationEvent event)
    {
        showStatus(event.getStep(), event.getWeather().toString(), event.getTimeText(), event.getField());
    }

    @Override
    public void simulationReset(SimulationEvent event)
    {
        showStatus(event.getStep(), event.getWeather().toString(), event.getTimeText(), event.getField());
    }

    private Color getColor(Class<?> animalClass)
    {
        if(!controlPanel.isSpeciesSelected(animalClass)) {
            return EMPTY_COLOR;
        }
        Color color = colors.get(animalClass);
        if(color == null) {
            return UNKNOWN_COLOR;
        }
        return color;
    }
}
