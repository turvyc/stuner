import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class STuner {

    private static int bufferSize = 4096;
    private static int bytesPerFrame = 2;

    public static double sampleRate = 8000;						// frames per second
    public static int bitDepth = 16;								// sample size in bits
    public static int channels = 1;
    public static boolean signed = true;
    public static boolean bigEndian = false;

    public static void main(String args[]) {    	
        TargetDataLine microphone = getMicrophone();        
        PitchDetector detector = new PitchDetector();
        PitchComparator comparator = new PitchComparator(); 
        GUIListener listener = new GUIListener(comparator);
        TunerFrame frame = new TunerFrame(listener);
        comparator.addObserver(frame);
        frame.setVisible(true);        

        // --------------------------------------------------------------------
        // USING SOUND FILE
        // --------------------------------------------------------------------
        File fileIn = new File("low-e.wav");
        
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
      	  	bytesPerFrame = audioInputStream.getFormat().getFrameSize();
      	  	sampleRate = audioInputStream.getFormat().getSampleRate();
      	  	
      	  	if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
      	  		bytesPerFrame = 1;
      	  	}
      	  	      	  
      	  	byte[] audioBytes = new byte[(int) audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize()];
      	  
      	  	try {
                    audioInputStream.read(audioBytes);
      	  			double pitch = detector.getPitch(audioBytes);
      	  		
      	  	} catch (Exception e) { 
                e.printStackTrace();
      	  	}
      	} catch (Exception e) {
            e.printStackTrace();
      	}
    	
        /*
        // --------------------------------------------------------------------
        // USING MICROPHONE
        // --------------------------------------------------------------------
        byte[] audioBytes = new byte[bufferSize * bytesPerFrame];
        microphone.start();
               
        while (true) {
            microphone.read(audioBytes, 0, audioBytes.length);
            double pitch = detector.getPitch(audioBytes);
            
            // update GUI
            int cent = comparator.comparePitch(pitch);
        }
        */
    }

    private static TargetDataLine getMicrophone() {

        AudioFormat format = new AudioFormat((float) sampleRate, bitDepth, channels, signed, bigEndian);                      
        TargetDataLine mic = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
        
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
