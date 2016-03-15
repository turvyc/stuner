/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitchdetector;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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
        
    boolean stopped;
    int numBytesRead;
    float [] f_buffer;
		
    //-------------------------------------------------------------------------	
    public PitchDetector(){
		
        sampleRate = 8000.0f;                                                   //8000, 11025, 16000, 22050, 44100    - samples/sec
        sampleSizeInBits = 16;                                                  // 8, 16
        channels = 1;                                                           // 1 - mono, 2-stereo
        signed = true;                                                          
        bigEndian = false;
        
        stopped = false;	
    }
	
	
    //-------------------------------------------------------------------------
    public void captureAudio(){

        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);                      
        TargetDataLine line = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
        
        if (!AudioSystem.isLineSupported(info)) {
            System.out.print("\nAudio Line not supported");
            System.exit(-1);
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            System.out.print("\nLine Unavailable: " + ex + "\n");
            System.exit(-2);
        }
                     
        byte[] buffer = new byte[line.getBufferSize() / 5];        
        f_buffer = new float[buffer.length / 4];                                // 4 bytes = 1 float

        line.start();

        while (!stopped) {
    	 
            numBytesRead =  line.read(buffer, 0, buffer.length);            
            printBuffer(buffer);                                                // DEBUG LINE
                        
            f_buffer = convToFloat(buffer);
            printFBuffer(f_buffer);                                             // DEBUG LINE
                                               
        }          
    }
		    
    //-------------------------------------------------------------------------
    // This method accepts byte array, converts its values to float (4 bytes = 1 float) and 
    //             returns float array with converted values.
    // @param:  source  - the byte array to be converted
    // @return: fArr    - float array with converted values  
    //-------------------------------------------------------------------------
    public static float[] convToFloat(byte [] source){

        ByteArrayInputStream bas = new ByteArrayInputStream(source);
    
        DataInputStream ds = new DataInputStream(bas);
        float[] fArr = new float[source.length / 4];  // 4 bytes per float
        
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