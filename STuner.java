import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class STuner {

    private static int bufferSize = 2048;
    private static int bytesPerFrame = 2;

    public static int sampleRate = 8000;						// frames per second
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

        /*
        // --------------------------------------------------------------------
        // USING SOUND FILE
        // --------------------------------------------------------------------
    	int totalFramesRead = 0;
        File fileIn = new File("146.83Hz.wav");
        
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
      	  	bytesPerFrame = audioInputStream.getFormat().getFrameSize();
      	  	sampleRate = audioInputStream.getFormat().getSampleRate();
      	  	
      	  	System.out.print("\n146.83Hz.wav, 5 sec, " + sampleRate + 
                    " Hz, 16bit, Bytes per frame: " + bytesPerFrame);
      	  	
      	  	if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
      	  		bytesPerFrame = 1;
      	  	}
      	  	      	  
      	  	// Set an arbitrary buffer size of 1024 frames.
      	  	int numBytes = 1024 * bytesPerFrame;
      	  	System.out.print(", Current buffer size: " + numBytes);
      	  	byte[] audioBytes = new byte[numBytes];
      	  
      	  	try {
      	  		int numBytesRead = 0;
      	  		int numFramesRead = 0;
      	  		// Try to read numBytes bytes from the file.
      	  		while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {

      	  			// Calculate the number of frames actually read.
      	  			numFramesRead = numBytesRead / bytesPerFrame;
      	  			totalFramesRead += numFramesRead;
      	  			
      	  			//System.out.print("\nNumber of bytes read: " + numBytesRead);
      	  			//System.out.print(", Total frames read: " + totalFramesRead);
      	  			
      	  			double pitch = detector.getPitch(audioBytes, sampleRate);
      	  			//System.out.print("\n       Pitch: " + pitch);
      	  			
      	  		}
      	  		
      	  		
      	  	} catch (Exception ex) { 
      	  		System.out.print("File Read error" + ex);
      	  	}
      	} catch (Exception e) {
      		System.out.print("I/O error" + e);
      	}
    	
        */
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
    }

    private static TargetDataLine getMicrophone() {

        AudioFormat format = new AudioFormat(sampleRate, bitDepth, channels, signed, bigEndian);                      
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
