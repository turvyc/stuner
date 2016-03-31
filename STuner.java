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
    
    public static int x_scale = 400;                                            // Set it to the Wavelength Component width 
    public static int x_jump = BUFFER_SIZE / x_scale;
    public static int y_scale = 1;                                            // Set it to the 1/2 of the Component height
    public static int y_divisor = Short.MAX_VALUE / y_scale; 
    public static int y_shift = 100;
    
    

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
        WaveComponent waveform = new WaveComponent(400, 200, y_shift);
        TunerFrame frame = new TunerFrame(listener, waveform);
        comparator.addObserver(frame);
        //detector.addObserver(waveform);
        frame.setVisible(true);        

        // Create array to store audio data from microphone
        byte[] audioData = new byte[BUFFER_SIZE * BYTES_PER_FRAME];
        double[] samples = new double[audioData.length / 2];
        
        x_jump = (samples.length) / x_scale;

        // Start the microphone
        microphone.start();
        
        System.out.print("\n y_divisor: " + y_divisor);

        // Infinite loop, ends when user closes program
        while (true) {

            // Read data from the microphone into the audioData array
            microphone.read(audioData, 0, audioData.length);

            // convert audio bytes to samples
            samples = convToDouble(audioData);
            
            // Get the pitch from the current data
            double pitch = detector.getPitch(samples);
            // Compare the pitch with known values (automatically updates GUI)
            int cent = comparator.comparePitch(pitch);
            
            
            waveform.clear();
            for ( int i = 1; i < samples.length; i+= x_jump ){    		
            	waveform.addLine((i/x_jump)-1, -(int)samples[i-1]/y_divisor, i/x_jump, -(int)samples[i]/y_divisor);    		
            }  
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
    
    /**
     * Converts audio bytes stream to samples array.
     */
    private static double[] convToDouble(byte[] byteArr) {
        double[] doubleArr = new double[byteArr.length / 2];
        for (int i = 0; i < doubleArr.length; i++)
            doubleArr[i] = (double) (byteArr[2 * i + 1] << 8 | byteArr[2 * i]);        
        return doubleArr;
    }    
}
