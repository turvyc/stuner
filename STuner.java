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
    
    private static final int GRAPH_WIDTH = 500;
    private static final int GRAPH_HEIGHT = 200;
    private static final int Y_SHIFT = 100;
    
    public static int x_scale = GRAPH_WIDTH;                                  // Set it to the Wavelength Component width 
    public static int x_jump = BUFFER_SIZE / GRAPH_WIDTH;
    public static int y_scale = 2;                                            // Set it to the 1/2 of the Component height
    public static int y_divisor = Short.MAX_VALUE / y_scale; 
    public static int y_shift = GRAPH_HEIGHT / 2;
    
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
        WaveComponent waveform = new WaveComponent(GRAPH_WIDTH, GRAPH_HEIGHT, y_shift);
        TunerFrame frame = new TunerFrame(listener, waveform);
        comparator.addObserver(frame);
        //detector.addObserver(waveform);
        frame.setVisible(true);        

        // Create array to store audio data from microphone
        byte[] audioData = new byte[BUFFER_SIZE * BYTES_PER_FRAME];
        double[] samples = new double[audioData.length / 2];
        
        // Start the microphone
        microphone.start();
        
        System.out.print("\n y_divisor: " + y_divisor);

        // Infinite loop, ends when user closes program
        while (true) {

            // Read data from the microphone into the audioData array
            microphone.read(audioData, 0, audioData.length);

            // convert audio bytes to samples
            samples = convToDouble(audioData);
            
            // THREAD 1: Process data, get Pitch and Cents
            ProcessingThread pt = new ProcessingThread(detector, comparator, samples);
            pt.start();
                        
            // THREAD 2: Draw Waveform
            DrawingThread dt = new DrawingThread(waveform, samples, x_jump, y_divisor);
            dt.start();        
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

/**
 * PROCESSING THREAD - Computes the Pitch and Cents
 */
class ProcessingThread extends Thread{
    private double pitch;
    private int cent;
    double [] samples;
    
    private PitchDetector detector;
    private PitchComparator pc;
    
    public ProcessingThread (PitchDetector d, PitchComparator c, double[] data){
        pitch = 0.0;
        cent = 0;
        samples = data;
        
        detector = d;
        pc = c;                
    }
        
    public void run(){
        // Get the pitch from the current data
        double pitch = detector.getPitch(samples);
        // Compare the pitch with known values (automatically updates GUI)
        int cent = pc.comparePitch(pitch);            
    }
}

/**
 * DRAWING THREAD - Draws the sound waveform
 */
class DrawingThread extends Thread{
    private WaveComponent wavecomp;
    double [] data;
    int x_jump;
    int y_divisor;
    
    public DrawingThread(WaveComponent component, double[] samples, int x_jmp, int y_dvsr){
        wavecomp = component;
        data = samples;
        x_jump = x_jmp;
        y_divisor = y_dvsr;
    }
    
    public void run(){
        wavecomp.clear();
        // Add line to the Graph and repaint
        for ( int i = 1; i < data.length; i+= x_jump ){    		
            wavecomp.addLine((i/x_jump)-1, -(int)data[i-1]/y_divisor, i/x_jump, -(int)data[i]/y_divisor);    		
        }     
    }
}




