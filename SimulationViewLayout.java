import java.awt.*;
import javax.swing.*;

/**
 * Assembles the GUI layout for the simulation view: the info bar, action buttons,
 * and population panel. Wires button listeners to the GUIHandler and applies the
 * completed layout to the given frame.
 *
 * @version 2022.02.28
 */
public class SimulationViewLayout
{
    private final GUIHandler handler;

    /**
     * @param handler (GUIHandler) The handler that button actions are forwarded to.
     */
    public SimulationViewLayout(GUIHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Build all panels and buttons, attach action listeners, and add them to the
     * given frame. Calls pack() and setVisible(true) on the frame when done.
     *
     * @param frame (JFrame) The frame to populate (typically the SimulatorView itself).
     * @param fieldView (FieldView) The grid rendering component.
     * @param population (JPanel) The population statistics panel.
     * @param stepLabel (JLabel) Label for the step counter.
     * @param infoLabel (JLabel) Label for short info text.
     * @param timeLabel (JLabel) Label for the current time.
     * @param temperatureLabel (JLabel) Label for the current temperature.
     * @param seasonLabel (JLabel) Label for the current season.
     */
    public void buildAndApply(JFrame frame, FieldView fieldView, JPanel population,
                               JLabel stepLabel, JLabel infoLabel, JLabel timeLabel,
                               JLabel temperatureLabel, JLabel seasonLabel)
    {
        FlowLayout simInfo = new FlowLayout();
        simInfo.setHgap(50);
        JPanel infoPane = new JPanel(simInfo);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(stepLabel, BorderLayout.CENTER);
        infoPane.add(timeLabel, BorderLayout.CENTER);
        infoPane.add(seasonLabel, BorderLayout.CENTER);
        infoPane.add(temperatureLabel, BorderLayout.CENTER);

        JButton launchLongSimButton = new JButton("Launch long simulation");
        launchLongSimButton.addActionListener(e -> handler.launchLongSimulation());

        JButton launchHundredStepsButton = new JButton("Run 100 steps");
        launchHundredStepsButton.addActionListener(e -> handler.runHundredSteps());

        JButton launchOneStepButton = new JButton("Run 1 step");
        launchOneStepButton.addActionListener(e -> handler.runOneStep());

        JButton goBackMenuButton = new JButton("Run a new simulation");
        goBackMenuButton.addActionListener(e -> {
            frame.setVisible(false);
            handler.switchToMenuView();
        });

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(launchLongSimButton);
        buttons.add(launchHundredStepsButton);
        buttons.add(launchOneStepButton);
        buttons.add(goBackMenuButton);

        Box bottomComponents = Box.createVerticalBox();
        bottomComponents.add(population);
        bottomComponents.add(buttons);

        frame.add(infoPane, BorderLayout.NORTH);
        frame.add(fieldView, BorderLayout.CENTER);
        frame.add(bottomComponents, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
