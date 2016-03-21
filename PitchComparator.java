import java.util.EnumMap;
import java.util.Observable;

public class PitchComparator extends Observable {

    public static enum GuitarString {E1, A, D, G, B, E6};
    // Represents a guitar string
    
    private double[] stringFrequencies = {
        82.41, 110.0, 146.83, 196.0, 246.94, 329.63
    };
    // The fundamental frequencies of an in-tune string, in order of the 
    // values in GuitarString

    private EnumMap<GuitarString, Double> tuningMap;
    // Maps a GuitarString to its correct fundamental frequency

    private GuitarString currentString;
    // The current string being tuned

    private boolean autoMode;
    // Whether it is in auto mode or not

    private int cents;
    // The difference in cents between the input signal and the correct tuning

    private int N_STRINGS = 6;
    // The number of strings on a guitar

    public PitchComparator() {
        // Map the guitar strings to their respective frequency
        tuningMap = new EnumMap<GuitarString, Double>(GuitarString.class);
        for (int i = 0; i < GuitarString.values().length; i++)
            tuningMap.put(GuitarString.values()[i], stringFrequencies[i]);

        currentString = GuitarString.E1;
        autoMode = false;
    }

    public int comparePitch(double pitch) {
        cents = calculateCents(pitch, tuningMap.get(currentString));
        setChanged();
        notifyObservers(cents);
        return cents;
    }

    private int calculateCents(double a, double b) {
        final int CENT_CONSTANT = 1200;
        final int BASE_2 = 2;
        double ratio = a / b;
        return (int) (CENT_CONSTANT * (Math.log(ratio) / Math.log(BASE_2)));
    }

    public void setAutoMode(boolean b) {
        autoMode = b;
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public void setCurrentString(GuitarString s) {
        currentString = s;
        setChanged();
        notifyObservers(cents);
    }

    public void stepString() {
        switch (currentString) {
            case E1:
                currentString = GuitarString.A;
                break;
            case A:
                currentString = GuitarString.D;
                break;
            case D:
                currentString = GuitarString.G;
                break;
            case G:
                currentString = GuitarString.B;
                break;
            case B:
                currentString = GuitarString.E6;
                break;
            case E6:
                currentString = GuitarString.E1;
                break;
        }
        setChanged();
        notifyObservers(cents);
    }

    public GuitarString getCurrentString() {
        return currentString;
    }
}
