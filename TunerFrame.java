
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TunerFrame extends JFrame implements Observer {

    private final int CENT_THRESHOLD = 10;

    // GUI Text
    public static String TITLE_TEXT = "sTuner";
    public static String STEP_BUTTON_TEXT = "Next string";
    public static String AUTO_BUTTON_TEXT = "Auto mode";
    public static String LOW_E_TEXT = "E";
    public static String A_TEXT = "A";
    public static String D_TEXT = "D";
    public static String G_TEXT = "G";
    public static String B_TEXT = "B";
    public static String HIGH_E_TEXT = "E";

    // GUI components
    private JButton stepButton;
    private JButton autoButton;
    private JLabel stringLabel;
    private JLabel centLabel;
    private JLabel flatLabel;
    private JLabel sharpLabel;
    private JLabel flatIndicator;
    private JLabel sharpIndicator;
    private WaveComponent waveform;
    
    // ActionListener
    private GUIListener listener;

    public TunerFrame(GUIListener l) {
        listener = l;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(TITLE_TEXT);
        setupLabels();
        add(setupMainPanel());        
        pack();
    }

    public void update(Observable o, Object arg) {
        // Cast the information from the observable (PitchComparator)
        int cents = (Integer) arg;
        PitchComparator comparator = (PitchComparator) o;

        // Update text
        centLabel.setText(String.format("%d", cents));
        setCurrentString(comparator.getCurrentString());

        // Update indicator labels
        if (Math.abs(cents) < CENT_THRESHOLD) {
            sharpIndicator.setBackground(Color.GREEN);
            flatIndicator.setBackground(Color.GREEN);
        }
        else if (cents < 0) {
            sharpIndicator.setBackground(new Color(0, 0, 0, 0));
            flatIndicator.setBackground(Color.RED);
        }
        else if (cents > 0) {
            flatIndicator.setBackground(new Color(0, 0, 0, 0));
            sharpIndicator.setBackground(Color.RED);
        }
    }


    private void setCurrentString(PitchComparator.GuitarString s) {
        switch (s) {
            case E1:
                stringLabel.setText(LOW_E_TEXT);
                break;
            case A:
                stringLabel.setText(A_TEXT);
                break;
            case D:
                stringLabel.setText(D_TEXT);
                break;
            case G:
                stringLabel.setText(G_TEXT);
                break;
            case B:
                stringLabel.setText(B_TEXT);
                break;
            case E6:
                stringLabel.setText(HIGH_E_TEXT);
                break;
        }
    }

    private void setupLabels() {
        final float LARGE_FONT_SIZE = 48.0f;
        String FLAT_SIGN = "\u266d";
        String SHARP_SIGN = "\u266f";
        Dimension INDICATOR_SIZE = new Dimension(25, 25);
        Border border = LineBorder.createBlackLineBorder();

        flatIndicator = new JLabel();
        flatIndicator.setOpaque(true);
        flatIndicator.setBorder(border);
        flatIndicator.setPreferredSize(INDICATOR_SIZE); 

        flatLabel = new JLabel();
        flatLabel.setFont(flatLabel.getFont().deriveFont(LARGE_FONT_SIZE));
        flatLabel.setText(FLAT_SIGN);

        sharpIndicator = new JLabel();
        sharpIndicator.setOpaque(true);
        sharpIndicator.setBorder(border);
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
        
        waveform = new WaveComponent(400, 200, 100);
        waveform.setBorder(BorderFactory.createLineBorder(Color.black));
        
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
        stepButton.addActionListener(listener);
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 2;
        panel.add(stepButton, c);

        // Add the auto button
        autoButton = new JButton(AUTO_BUTTON_TEXT);
        autoButton.addActionListener(listener);
        c.gridx = 4;
        panel.add(autoButton, c);
        
        
        // Add the Waveform
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 6;
        c.gridheight = 3;
        panel.add(waveform, c);
        
        
        return panel;
    }

}
