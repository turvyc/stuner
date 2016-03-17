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
import org.jtransforms.fft.FloatFFT_1D;

public class PitchDetector {

    private boolean stopped;
    private int numBytesRead;

    private final int FUNDAMENTAL_FREQUENCY_INDEX = 1;

    //-------------------------------------------------------------------------
    public float getPitch(byte[] audioBytes) {
        float[] audioFloats = convToFloat(audioBytes);
        printBuffer(audioBytes);
        printFBuffer(audioFloats);
        FloatFFT_1D fft = new FloatFFT_1D(audioFloats.length);
        fft.realForward(audioFloats);
        System.out.print("\nafter fft: ");
        printFBuffer(audioFloats);
        return audioFloats[FUNDAMENTAL_FREQUENCY_INDEX];
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
}
