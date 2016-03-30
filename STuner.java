import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Main class.
 */
public class STuner {

    public static final int SAMPLE_RATE = 8000;
    // Samples per second

    private static final int BUFFER_SIZE = 8192;
    // Number of samples in the microphone data buffer

    private static final int BYTES_PER_FRAME = 2;
    // Each sample is 2 bytes or 16 bits

    private static final int BIT_DEPTH = 16;
    // Sample size in bits

    private static final int CHANNELS = 1;
    // Mono recording from microphone
    
    private static final boolean SIGNED = true;
    // Data bytes are signed

    private static final boolean BIG_ENDIAN = false;
    // Data bytes are little endian

    /**
     * Opens microphone, initializes classes, and runs main loop.
     */
    public static void main(String args[]) {    	
        // Get a line on the microphone
        TargetDataLine microphone = getMicrophone();

        // Set up application objects
        PitchDetector detector = new PitchDetector();
        PitchComparator comparator = new PitchComparator(); 
        GUIListener listener = new GUIListener(comparator);
        TunerFrame frame = new TunerFrame(listener);
        comparator.addObserver(frame);
        frame.setVisible(true);        

        // Create array to store audio data from microphone
        byte[] audioData = new byte[BUFFER_SIZE * BYTES_PER_FRAME];

        // Start the microphone
        microphone.start();

        // Infinite loop, ends when user closes program
        while (true) {

            // Read data from the microphone into the audioData array
            microphone.read(audioData, 0, audioData.length);

            // Get the pitch from the current data
            double pitch = detector.getPitch(audioData);

            // Compare the pitch with known values (automatically updates GUI)
            int cent = comparator.comparePitch(pitch);
        }
    }

    /**
     * Sets up microphone.
     */
    private static TargetDataLine getMicrophone() {

        // Prepare the audio format
        AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BIT_DEPTH, CHANNELS, SIGNED, BIG_ENDIAN);
        TargetDataLine mic = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // Check that the microphone is supported
        if (!AudioSystem.isLineSupported(info)) {
            System.out.print("\nAudio Line not supported");
            System.exit(-1);
        }

        // Open the microphone
        try {
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);

        } catch (LineUnavailableException ex) {
            System.out.print("\nLine Unavailable: " + ex + "\n");
            System.exit(-2);
        }

        return mic;
    }
}
