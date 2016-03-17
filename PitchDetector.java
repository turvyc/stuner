/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;



public class PitchDetector {
	
    private static float sampleRate;
    private static int sampleSizeInBits;
    private static int channels;
    private boolean signed;
    private boolean bigEndian;
    
    private int bufferSize;
    private int bytesPerFrame;
        
    private boolean stopped;
    private int numBytesRead;
		
    //-------------------------------------------------------------------------	
    public PitchDetector(){
		
        sampleRate = 8000.0f;                                                   //8000, 11025, 16000, 22050, 44100    - samples/sec
        sampleSizeInBits = 16;                                                  // 8, 16-audio CD quality
        channels = 1;                                                           // 1-mono, 2-stereo
        signed = true;                                                          
        bigEndian = false;														// using littleEndian. ByteBuffer (bigEndian)
        
        bufferSize = 512;
        bytesPerFrame = 1;
        
        stopped = false;
        numBytesRead = 0;
    }
	
	
    //-------------------------------------------------------------------------
    public void captureAudio(){

        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);                      
        TargetDataLine microphone = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object

        
        
        if (!AudioSystem.isLineSupported(info)) {
            System.out.print("\nAudio Line not supported");
            System.exit(-1);
        }

        // ----- OPEN microphone
        try {
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            
        } catch (LineUnavailableException ex) {
            System.out.print("\nLine Unavailable: " + ex + "\n");
            System.exit(-2);
        }
        
        int numBytes = bufferSize * bytesPerFrame;
        
        byte[] audioBytes = new byte[numBytes];        
        float [] audioFloats = new float[audioBytes.length / 4];                                // 4 bytes = 1 float

        
        microphone.start();

        // --- READ data
        while (!stopped) {
    	 
            numBytesRead =  microphone.read(audioBytes, 0, audioBytes.length);            
            printBuffer(audioBytes);                                                // DEBUG LINE
                        
            audioFloats = convToFloat(audioBytes);
            printFBuffer(audioFloats);                                             // DEBUG LINE
                                               
        }          
    }
		    
    //-------------------------------------------------------------------------
    // This method accepts byte array, converts its values to float (4 bytes = 1 float) and 
    //             returns float array with converted values.
    // @param:  byteArr  - the byte array to be converted
    // @return: fArr    - float array with converted values  
    //-------------------------------------------------------------------------
    public static float[] convToFloat(byte [] byteArr){

        
        /*
        
        final int BYTES_PER_FLOAT = 4;
        final int BITS_PER_BYTE = 8;

        float[] audioFloats = new float[byteArr.length / BYTES_PER_FLOAT];

        for (int i = 0; i < audioFloats.length; i++) {
            int result = 0;
            for (int j = 0; j < BYTES_PER_FLOAT; j++) {
                int tmp = byteArr[(i * BYTES_PER_FLOAT) + j];
                tmp = tmp << j * BITS_PER_BYTE;
                result = result | tmp;
            }
            audioFloats[i] = Float.intBitsToFloat(result);

        }
        return audioFloats;

        */

    	
        ByteArrayInputStream bas = new ByteArrayInputStream(byteArr);    
        DataInputStream ds = new DataInputStream(bas);
        
        float[] fArr = new float[byteArr.length / 4];  // 4 bytes per float
        
        for (int i = 0; i < fArr.length; i++){
        
            try {
                fArr[i] = ds.readFloat();
                
            } catch (IOException ex) {
                Logger.getLogger(PitchDetector.class.getName()).log(Level.SEVERE, null, ex);
                System.out.print("\nI/O Exception: " + ex);
                System.exit(-2);
            }
        }
  
        return fArr;

    }
    
    
    // DEBUG HELPERS
    //-------------------------------------------------------------------------
    public void printBuffer(byte[] bfr){
		
        System.out.print("\n\nByte Buffer size: " + numBytesRead + "\n");
        
        for(int i = 0; i < bfr.length; i++){        	
        	System.out.print(" " + bfr[i]);
        }			
    }
    
    
    
    public void printFBuffer(float[] bfr){
		
        System.out.print("\n\nFloat Buffer size:" + bfr.length + "\n");
        
        for(int i = 0; i < bfr.length; i++){        	
        	System.out.print(" " + bfr[i]);
        }			
    }
    
    
	
	
	
	
	
    
    // ENABLED FOR UNIT TESTING	
    //-------------------------------------------------------------------------
    public static void main(String args[]){
		
	PitchDetector p1 = new PitchDetector();
	p1.captureAudio();
		
		
    }
    
   
    
}
