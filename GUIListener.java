import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIListener implements ActionListener {

    private PitchComparator comparator;

    public GUIListener(PitchComparator c) {
        comparator = c;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(TunerFrame.STEP_BUTTON_TEXT))
            comparator.stepString();
        if (e.getActionCommand().equals(TunerFrame.AUTO_BUTTON_TEXT))
            comparator.setAutoMode(! comparator.isAutoMode());
    }
}
