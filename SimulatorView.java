import java.awt.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2022/03/02
 */
public class SimulatorView extends JFrame
{
    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DAYOFTIME_PREFIX = "Time: It's ";
    private final String POPULATION_DIE_OF_DISEASE_PREFIX = "Population died of disease: ";
    
    
    private JLabel stepLabel, population, infoLabel, diseaseLabel;
    private FieldView fieldView;
    
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();

        setTitle("Underwater Environment Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(DAYOFTIME_PREFIX + " ", JLabel.CENTER);
        diseaseLabel = new JLabel(POPULATION_DIE_OF_DISEASE_PREFIX, JLabel.CENTER);
        
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(diseaseLabel, BorderLayout.EAST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        fieldView.setColor(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param weather the object of weather class
     * @param oxygenLevel the oxygen level that is to be displayed in the current step.
     */
    public void showStatus(int step, Field field, boolean timeOfDay, Weather weather, double oxygenLevel)
    {
         if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        infoLabel.setText("It is: " + (timeOfDay? "daytime": "night ") 
                            + "        Oxygen Level: " + (int)(oxygenLevel * 100) + "%"
                            + "        Storm: " + (weather.getStormStart()? "exists": "subsides"));
        diseaseLabel.setText(POPULATION_DIE_OF_DISEASE_PREFIX + Animal.populationDieOfDisease);
        stats.reset();
        fieldView.render(field, weather, stats);
        
        stats.countFinished();
        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
    }
    
    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
}
