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

        /*
        // low-pass filter
        float currentValue;
        float value = audioFloats[0];
        float smoothing = 20.0f;
        for (int i = 1; i < audioFloats.length; i++) {
            currentValue = audioFloats[i];
            value += (currentValue - value) / smoothing;
            audioFloats[i] = value;
        }

        */
        // Perform FFT
        FloatFFT_1D fft = new FloatFFT_1D(audioFloats.length);        
        fft.realForward(audioFloats);

        // Find the greatest magnitude, which is the fundamental frequency
        int maxIndex = -1;
        double maxMagnitude = Double.NEGATIVE_INFINITY;
        double magnitude; 

        for(int i = 2; i < audioFloats.length; i+=2 ) {

        	// get magnitude
        	magnitude = 
        	Math.sqrt( (double) audioFloats[i] * audioFloats[i] + audioFloats[i+1] * audioFloats[i+1] );
        	
        	// compare current magnitude to max magnitude        	
            if(magnitude > maxMagnitude) {
                maxIndex = i;
                maxMagnitude = magnitude;
            }
        }
        
        pitch = (double) STuner.sampleRate * (maxIndex / 2.0) / (double) audioFloats.length;           

        System.out.println("Pitch: " + pitch);
        return pitch;
    }
    
    public static float[] convToFloat(byte[] byteArr) {
        float[] floatArr = new float[byteArr.length / 2];
        for (int i = 0; i < floatArr.length; i++)
            floatArr[i] = (float) (byteArr[2 * i + 1] << 8 | byteArr[2 * i]);
        return floatArr;
    }
}
