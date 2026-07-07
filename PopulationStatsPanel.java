import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 * A JPanel that displays per-species population checkboxes and the current
 * disease count. Owns a FieldStats instance; checkbox toggles push color
 * changes directly into the FieldRenderer.
 *
 * @version 2022.03.02
 */
class PopulationStatsPanel extends JPanel
{
    private final FieldStats stats;
    private final FieldRenderer renderer;
    private final SimulationViabilityEvaluator viabilityEvaluator;
    private final Map<Class<?>, JCheckBox> classToCheckBox;
    private final JLabel diseasedPopulation;

    PopulationStatsPanel(FieldRenderer renderer)
    {
        this.renderer = renderer;
        this.stats = new FieldStats();
        this.viabilityEvaluator = new SimulationViabilityEvaluator();

        JCheckBox grassCheckBox  = new JCheckBox("Grass: 0",  true);
        JCheckBox mouseCheckBox  = new JCheckBox("Mouse: 0",  true);
        JCheckBox deerCheckBox   = new JCheckBox("Deer: 0",   true);
        JCheckBox wolfCheckBox   = new JCheckBox("Wolf: 0",   true);
        JCheckBox coyoteCheckBox = new JCheckBox("Coyote: 0", true);
        JCheckBox hunterCheckBox = new JCheckBox("Hunter: 0", true);
        JCheckBox eagleCheckBox  = new JCheckBox("Eagle: 0",  true);

        diseasedPopulation = new JLabel("Diseased: 0", JLabel.LEFT);

        classToCheckBox = Map.ofEntries(
                Map.entry(Deer.class,   deerCheckBox),
                Map.entry(Coyote.class, coyoteCheckBox),
                Map.entry(Wolf.class,   wolfCheckBox),
                Map.entry(Mouse.class,  mouseCheckBox),
                Map.entry(Grass.class,  grassCheckBox),
                Map.entry(Hunter.class, hunterCheckBox),
                Map.entry(Eagle.class,  eagleCheckBox)
        );

        for(Class cls : SimulationInfo.ALL_ACTORS) {
            classToCheckBox.get(cls).setForeground(SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }

        // Each checkbox toggle immediately updates the renderer's color for that species.
        for(Map.Entry<Class<?>, JCheckBox> entry : classToCheckBox.entrySet()) {
            Class<?> cls = entry.getKey();
            JCheckBox box = entry.getValue();
            box.addItemListener(e -> renderer.updateColorForClass(cls, box.isSelected()));
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("POPULATIONS"));
        add(grassCheckBox);
        add(mouseCheckBox);
        add(deerCheckBox);
        add(wolfCheckBox);
        add(coyoteCheckBox);
        add(hunterCheckBox);
        add(eagleCheckBox);
        add(diseasedPopulation);
    }

    /**
     * Refresh checkbox text and the disease count label from the current stats.
     */
    public void updateDisplay()
    {
        for(Class cls : SimulationInfo.ALL_ACTORS) {
            classToCheckBox.get(cls).setText(stats.getCountDetails(cls));
        }
        diseasedPopulation.setText(stats.getDiseasedPopulation());
    }

    /**
     * Push the current checkbox visibility state into the renderer color map.
     * Called after each render pass so that any checkbox changes take effect.
     */
    public void syncColorsToRenderer()
    {
        for(Map.Entry<Class<?>, JCheckBox> entry : classToCheckBox.entrySet()) {
            renderer.updateColorForClass(entry.getKey(), entry.getValue().isSelected());
        }
    }

    public FieldStats getStats() { return stats; }
    public boolean isViable()    { return viabilityEvaluator.isViable(stats); }
}
