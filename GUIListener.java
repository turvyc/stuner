import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles button clicks in the GUI.
 */
public class GUIListener implements ActionListener {

    private PitchComparator comparator;

    public GUIListener(PitchComparator c) {
        comparator = c;
    }

    public void actionPerformed(ActionEvent e) {

        // Step button
        if (e.getActionCommand().equals(TunerFrame.STEP_BUTTON_TEXT))
            comparator.stepString();

        // Auto button
        if (e.getActionCommand().equals(TunerFrame.AUTO_BUTTON_TEXT))
            comparator.setAutoMode(! comparator.isAutoMode());
    }
}
