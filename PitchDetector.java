package stuner;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class PitchDetector {
	
	private static final float SAMPLE_RATE = 8000; 					//8000, 11025, 16000, 22050, 44100    - samples/sec
	private static final int SAMPLE_SIZE_IN_BITS = 16;				// 8, 16
	private static final int CHANNELS = 1;							// 1-mono, 2-stereo									
	private static final boolean SIGNED = true;						// true = both positive/negative values in sample, false = positive only
	private static final boolean BIG_ENDIAN = true;
	
	
	AudioFormat audioFormat;
	TargetDataLine line;
	DataLine.Info info;
	
		
	//-------------------------------------------------------------------------
	public PitchDetector(){
		audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);				
		info = new DataLine.Info(TargetDataLine.class, audioFormat);
		

	}
	
	//-------------------------------------------------------------------------
	public void captureAudio(){
		

		if (!AudioSystem.isLineSupported(info)) {
		    System.out.print("\nInput Error: This line is not supported"); 
		}
		
		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(audioFormat);
		} catch (LineUnavailableException ex) {
			System.out.print("\nException occured\n");
		    ex.printStackTrace(); 
		}
		
		

		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[line.getBufferSize() / 5];


		line.start();


		while (line.read(data, 0, data.length) > 0) {
			
		   // Read the next chunk of data from the TargetDataLine.
		   numBytesRead =  line.read(data, 0, data.length);
		   
		   // Save this chunk of data.
		   out.write(data, 0, numBytesRead);
		   
		   System.out.print("\ndata array: " + numBytesRead + "\n");
		   for (int i = 0; i < data.length; i++){
			   
			   System.out.print(" " + data[0]);
			   
		   }
		   
		}   
		
		
		
		
		
	}
	
	
	public static void main (String args[]){
		
		PitchDetector p1 = new PitchDetector();
		p1.captureAudio();
		
	}
	
	
	
	
	
	
	
	
	
	
	

}
