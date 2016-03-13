import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TunerFrame extends JFrame implements Observer {

    String TITLE_TEXT = "sTuner";
    String STEP_BUTTON_TEXT = "Next string";
    String AUTO_BUTTON_TEXT = "Auto mode";
    final int CENT_THRESHOLD = 10;
    
    // GUI components
    JButton stepButton;
    JButton autoButton;
    JLabel stringLabel;
    JLabel centLabel;
    JLabel flatLabel;
    JLabel sharpLabel;
    JLabel flatIndicator;
    JLabel sharpIndicator;
    
    public TunerFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(TITLE_TEXT);
        setupLabels();
        add(setupMainPanel());
        pack();
    }

    public void update(Observable o, Object arg) {
        int cents = (Integer) arg;
        centLabel.setText(String.format("%d", cents));
        if (Math.abs(cents) < CENT_THRESHOLD) {
            // TODO Light both indicators
        }
        else if (cents < 0) {
            // TODO Change flat indicator label
        }
        else if (cents > 0) {
            // TODO Change sharp indicator label
        }
    }

    public void setCurrentString(PitchComparator.GuitarString s) {
        // TODO Switch statement for GuitarString values
    }

    private void setupLabels() {
        final float LARGE_FONT_SIZE = 48.0f;
        String FLAT_SIGN = "\u266d";
        String SHARP_SIGN = "\u266f";
        Dimension INDICATOR_SIZE = new Dimension(25, 25);

        flatIndicator = new JLabel();
        flatIndicator.setOpaque(true);
        flatIndicator.setBackground(Color.red);
        flatIndicator.setPreferredSize(INDICATOR_SIZE); 

        flatLabel = new JLabel();
        flatLabel.setFont(flatLabel.getFont().deriveFont(LARGE_FONT_SIZE));
        flatLabel.setText(FLAT_SIGN);

        sharpIndicator = new JLabel();
        sharpIndicator.setOpaque(true);
        sharpIndicator.setBackground(Color.red);
        sharpIndicator.setPreferredSize(INDICATOR_SIZE); 

        sharpLabel = new JLabel();
        sharpLabel.setFont(sharpLabel.getFont().deriveFont(LARGE_FONT_SIZE));
        sharpLabel.setText(SHARP_SIGN);

        centLabel = new JLabel();
        centLabel.setFont(centLabel.getFont().deriveFont(LARGE_FONT_SIZE));
        centLabel.setText("-5");

        stringLabel = new JLabel();
        stringLabel.setFont(stringLabel.getFont().deriveFont(LARGE_FONT_SIZE));
        stringLabel.setText("D");
    }

    private JPanel setupMainPanel() {
        final double DEFAULT_WEIGHT = 0.5;
        final int PADDING = 3;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
            
        // Set the default GridBag settings
        c.weightx = DEFAULT_WEIGHT;
        c.weighty = DEFAULT_WEIGHT;
        c.ipadx = PADDING;
        c.ipady = PADDING;

        // Add the cent label
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 3;
        panel.add(centLabel, c);

        // Add the string label
        c.gridy = 4;
        panel.add(stringLabel, c);

        // Add the flat indicator
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        panel.add(flatIndicator, c);

        // Add the flat label
        c.gridy = 2;
        panel.add(flatLabel, c);

        // Add the sharp indicator
        c.gridx = 5;
        c.gridy = 1;
        panel.add(sharpIndicator, c);

        // Add the sharp label
        c.gridy = 2;
        panel.add(sharpLabel, c);

        // Add the step button
        stepButton = new JButton(STEP_BUTTON_TEXT);
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 2;
        panel.add(stepButton, c);

        // Add the auto button
        autoButton = new JButton(AUTO_BUTTON_TEXT);
        c.gridx = 4;
        panel.add(autoButton, c);

        return panel;
    }
}
