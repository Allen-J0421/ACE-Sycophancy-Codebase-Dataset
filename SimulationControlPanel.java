import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays simulation controls and population filters.
 */
public class SimulationControlPanel extends JPanel
{
    private static final List<Class<?>> SPECIES_DISPLAY_ORDER = Arrays.asList(
            Grass.class,
            Mouse.class,
            Deer.class,
            Wolf.class,
            Coyote.class,
            Hunter.class,
            Eagle.class);

    private final JButton oneStepButton;
    private final JButton stopButton;
    private final JButton longButton;
    private final JButton resetButton;
    private final JLabel diseasedPopulation;
    private final Map<Class<?>, JCheckBox> classToCheckBox;

    public SimulationControlPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        oneStepButton = new JButton("Simulate 1 step");
        longButton = new JButton("Play 4000 steps");
        stopButton = new JButton("Stop simulation");
        resetButton = new JButton("Reset simulation");

        add(oneStepButton);
        add(longButton);
        add(stopButton);
        add(resetButton);

        diseasedPopulation = new JLabel("Diseased: ", JLabel.LEFT);
        classToCheckBox = new LinkedHashMap<>();

        JPanel populationPanel = new JPanel();
        populationPanel.setLayout(new BoxLayout(populationPanel, BoxLayout.Y_AXIS));
        populationPanel.add(new JLabel("POPULATIONS"));
        for(Class<?> speciesClass : SPECIES_DISPLAY_ORDER) {
            JCheckBox checkBox = new JCheckBox(speciesClass.getSimpleName() + ": 0", true);
            classToCheckBox.put(speciesClass, checkBox);
            populationPanel.add(checkBox);
        }
        populationPanel.add(diseasedPopulation);

        add(populationPanel);
    }

    public void addOneStepButtonListener(ActionListener listener)
    {
        oneStepButton.addActionListener(listener);
    }

    public void addResetButtonListener(ActionListener listener)
    {
        resetButton.addActionListener(listener);
    }

    public void addStopButtonListener(ActionListener listener)
    {
        stopButton.addActionListener(listener);
    }

    public void addLongButtonListener(ActionListener listener)
    {
        longButton.addActionListener(listener);
    }

    public void addSpeciesFilterListener(Class<?> speciesClass, ItemListener listener)
    {
        classToCheckBox.get(speciesClass).addItemListener(listener);
    }

    public boolean isSpeciesSelected(Class<?> speciesClass)
    {
        return classToCheckBox.get(speciesClass).isSelected();
    }

    public void setSpeciesColor(Class<?> speciesClass, Color color)
    {
        classToCheckBox.get(speciesClass).setForeground(color);
    }

    public void updateSpeciesCount(Class<?> speciesClass, String countDetails)
    {
        classToCheckBox.get(speciesClass).setText(countDetails);
    }

    public void updateDiseasedPopulation(String diseasedPopulationDetails)
    {
        diseasedPopulation.setText(diseasedPopulationDetails);
    }
}
