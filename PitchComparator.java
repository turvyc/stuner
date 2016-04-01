import java.util.EnumMap;
import java.util.Observable;

public class PitchComparator extends Observable {
    
    private double[] stringFrequencies = {
        82.41, 110.0, 146.83, 196.0, 246.94, 329.63
    };
    // The fundamental frequencies of an in-tune string, in order of the 
    // values in GuitarString

    private int currentString;
    // The current string being tuned. Valid values are 0 - 5, representing
    // E-A-D-G-B-E.

    private boolean autoMode;
    // Whether it is in auto mode or not

    private double cents;
    // The difference in cents between the input signal and the correct tuning

    private int N_STRINGS = 6;
    // The number of strings on a guitar

    public PitchComparator() {
        currentString = 0;
        autoMode = false;
    }

    public double comparePitch(double pitch) {
        if (autoMode) {

            // Handle edge cases
            if (pitch <= stringFrequencies[0])
                currentString = 0;
            else if (pitch >= stringFrequencies[5])
                currentString = 5;

            // Find the two strings the pitch is between. (i and i - 1).
            else {
                int i = 1;
                while (pitch > stringFrequencies[i] && i < N_STRINGS)
                    i++;

                // Find the halfway point between the two string frequencies
                double halfway = (stringFrequencies[i] + stringFrequencies[i - 1]) / 2;

                currentString = (pitch > halfway) ? i : i - 1;
            }
        }

        cents = calculateCents(pitch, stringFrequencies[currentString]);
        setChanged();
        notifyObservers(cents);
        return cents;
    }

    private double calculateCents(double a, double b) {
        final int CENT_CONSTANT = 1200;
        final int BASE_2 = 2;
        double ratio = a / b;
        return (CENT_CONSTANT * (Math.log(ratio) / Math.log(BASE_2)));
    }

    public void setAutoMode(boolean b) {
        autoMode = b;
        setChanged();
        notifyObservers(cents);
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public void setCurrentString(int string) {
        currentString = string;
        setChanged();
        notifyObservers(cents);
    }

    public void stepString() {
        currentString = (currentString + 1) % N_STRINGS;
        setChanged();
        notifyObservers(cents);
    }

    public int getCurrentString() {
        return currentString;
    }
}
