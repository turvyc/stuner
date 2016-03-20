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

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

public class PitchDetector {
		
    //-------------------------------------------------------------------------
    public double getPitch(byte[] audioBytes, float sampleRate) {
    	
    	double pitch = 0.0d;    	
    	int sRate = (int) sampleRate;
    	
    	// short[] audioShorts = convToShort(audioBytes);

        float[] audioFloats = convToFloat(audioBytes);
                
        //printBuffer(audioBytes);
        //printFBuffer(audioFloats, "");
  
        FloatFFT_1D fft = new FloatFFT_1D(audioFloats.length);        
        fft.realForward(audioFloats);
        //printFBuffer(audioFloats, "(After FFT): ");

        /* find the peak magnitude and it's index */
        int maxIndex = -1;
        double maxMagnitude = Double.NEGATIVE_INFINITY;
        double magnitude; 

        for(int i = 2; i < audioFloats.length; i+=2 ) {

        	// get magnitude
        	magnitude = 
        	Math.sqrt( (double) audioFloats[i] * audioFloats[i] + audioFloats[i+1] * audioFloats[i+1] );
        	
        	// System.out.print("\nMagnitude: " + magnitude);
        	
        	// compare current magnitude to max magnitude        	
            if(magnitude > maxMagnitude) {
                maxIndex = i;
                maxMagnitude = magnitude;
            }
        }
        
        System.out.print("\nMaxMag: " + maxMagnitude + "   ");
        System.out.print("       ,k index: " + maxIndex/2 + "   ");
        
        pitch =  ((double) sampleRate * (maxIndex/2) / (audioFloats.length));           
        System.out.print("       Pitch: " + pitch);

        return pitch;
    }
    
    //-------------------------------------------------------------------------
    // This method accepts byte array, converts its values to float (4 bytes = 1 float) and 
    //             returns float array with converted values.
    // @param:  byteArr  - the byte array to be converted
    // @return: fArr    - float array with converted values  
    //-------------------------------------------------------------------------
    public static float[] convToFloat(byte [] byteArr){
    	
        ByteArrayInputStream bas = new ByteArrayInputStream(byteArr);    
        DataInputStream ds = new DataInputStream(bas);
        
        float[] fArr = new float[byteArr.length / 2];  								// 2 bytes per short
        
        for (int i = 0; i < fArr.length; i++){
        
            try {            	
                fArr[i] = (float) ds.readShort();
                
            } catch (IOException ex) {
                System.out.print("\nI/O Exception: " + ex);
                System.exit(-2);
            }
        }
  
        return fArr;
    }
    
    /*
    //-------------------------------------------------------------------------
    public static short[] convToShort(byte [] byteArr){
    	
        ByteArrayInputStream bas = new ByteArrayInputStream(byteArr);    
        DataInputStream ds = new DataInputStream(bas);
        
        short[] sArr = new short[byteArr.length / 2];  								// 2 bytes per short
        
        for (int i = 0; i < sArr.length; i++){
        
            try {            	
                sArr[i] = ds.readShort();
                
            } catch (IOException ex) {
                System.out.print("\nI/O Exception: " + ex);
                System.exit(-2);
            }
        }
  
        return sArr;
    }
    
    */
    

    
    // DEBUG HELPERS
    //-------------------------------------------------------------------------
    public void printBuffer(byte[] bfr){
		
        System.out.print("\nByte Buffer:");
        
        for(int i = 0; i < bfr.length; i++){        	
        	System.out.print(" " + bfr[i]);
        }			
    }
    
    public void printFBuffer(float[] bfr, String msg){
		
        System.out.print("\n\nFloat Buffer size:" + msg + bfr.length + "\n");
        
        for(int i = 0; i < bfr.length; i++){        	
        	System.out.print(" " + bfr[i]);
        }			
    }
}
