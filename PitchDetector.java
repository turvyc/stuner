import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

public class PitchDetector {
		
    public double getPitch(byte[] audioBytes) {
    	
    	double pitch = 0.0d;    	
    	int sRate = (int) STuner.sampleRate;
        float[] audioFloats = convToFloat(audioBytes);
                        
        int N = 21;
        
        // Apply low-pass AVG filter
        for (int i = 0; i < audioFloats.length - N; i++){
        
           float temp_sum = 0.0f;           
           for (int j = i; j < i+N; j++  )  						// Accumulate         
               temp_sum += audioFloats[j];                      
                                  
            audioFloats[i] = temp_sum / 5;                    
        }

        // Perform FFT
        FloatFFT_1D fft = new FloatFFT_1D(audioFloats.length);        
        fft.realForward(audioFloats);

        /*
        // Downsample
        float[] halfSize = new float[audioFloats.length / 2];
        float[] thirdSize = new float[audioFloats.length / 3];

        for (int i = 0; i < halfSize.length; i += 2) {
            halfSize[i] = audioFloats[i * 2];
            halfSize[i + 1] = audioFloats[i * 2 + 1];
        }

        for (int i = 0; i < thirdSize.length - 1; i += 2) {
            thirdSize[i] = audioFloats[i * 3];
            thirdSize[i + 1] = 
                audioFloats[i * 3 + 1];
        }

        float[] productFloat = new float[thirdSize.length];
        for (int i = 0; i < thirdSize.length - 1; i += 2) {
            
            float[] tmp = multiplyComplex(audioFloats[i], audioFloats[i + 1],
                    halfSize[i], halfSize[i + 1]);
            
            tmp = multiplyComplex(tmp[0], tmp[1], thirdSize[i], thirdSize[i + 1]);
            
            productFloat[i] = tmp[0];
            productFloat[i + 1] = tmp[1];
        }
        */

        // Find the greatest magnitude, which is the fundamental frequency
        int maxIndex = -1;
        double maxMagnitude = Double.NEGATIVE_INFINITY;
        double magnitude; 

        for(int i = 2; i < audioFloats.length - 1; i+=2 ) {

        	// get magnitude
        	magnitude = Math.sqrt(audioFloats[i] * audioFloats[i] + 
                    audioFloats[i+1] * audioFloats[i+1]);
        	
        	// compare current magnitude to max magnitude        	
            if(magnitude > maxMagnitude) {
                maxIndex = i;
                maxMagnitude = magnitude;
            }
        }
        
        pitch = (double) STuner.sampleRate * (maxIndex / 2.0) / (double) audioFloats.length;           

                
        // CRAZY DIRTY ADJUSTING        
        float D = 4.0f;    														   // +/- Deviation
        
        //                       0=E1       1=A        2=D       3=G       4=B       5=E6
        float freqRangeL[] = { 82.41f-D, 110.0f-D, 146.83f-D, 196.0f-D, 246.94f-D, 329.63f-D };
        float freqRangeH[] = { 82.41f+D, 110.0f+D, 146.83f+D, 196.0f+D, 246.94f+D, 329.63f+D };
        
        boolean match = false;                
        while (!match){
                    
            if (pitch > freqRangeH[2] && pitch < freqRangeL[3])                    // check for 2* E1
                pitch /= 2;                        
                        
            if (pitch > freqRangeH[3] && pitch < freqRangeL[4])                    // check for 2* A
                pitch /= 2;                        
                        
            if (pitch > freqRangeH[4] && pitch < freqRangeL[5])                    // check for 2* D
                pitch /= 2;                        
                        
            if (pitch > freqRangeH[5])                    // check for 2* D
                pitch /= 2;                        
                                    
            match = true;                
        }

        System.out.println("Pitch: " + pitch);
        return pitch;
    }
    
    //-------------------------------------------------------------------------
    public static float[] convToFloat(byte[] byteArr) {
        float[] floatArr = new float[byteArr.length / 2];
        for (int i = 0; i < floatArr.length; i++)
            floatArr[i] = (float) (byteArr[2 * i + 1] << 8 | byteArr[2 * i]);
        return floatArr;
    }

    //-------------------------------------------------------------------------
    private float[] multiplyComplex(float r1, float i1, float r2, float i2) {
        float[] product = new float[2];
        product[0] = r1 * r2 - i1 * i2;
        product[1] = r1 * i2 + i1 * r2;
        return product;
    }    

}