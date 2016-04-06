import org.jtransforms.fft.DoubleFFT_1D;
import java.util.Observable;

/**
 * Determines the pitch, or fundamental frequency, of a signal.
 */
public class PitchDetector {
		
    /**
     * Determines the pitch, or fundamental frequency, of a signal.
     * @param samples an audio signal in the time domain
     * @return the fundamental frequency of the audio signal
     */
    public double getPitch(double[] samples) {

        // Use FFT to convert from time domain to frequency domain
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);        
        fft.realForward(samples);

        // Determine the harmonic product spectrum
        double[] hps = getHarmonicProductSpectrum(samples);

        // Determine the index of the frequency with the maximum magnitude
        int maxIndex = getMaxIndex(hps);

        // Calculate and return the fundamental frequency
        return STuner.SAMPLE_RATE * maxIndex / samples.length;           
    }

    /**
     * Calculates the Harmonic Product Spectrum of a frequency-domain signal.
     */
    private double[] getHarmonicProductSpectrum(double[] samples) {
        // Downsample the original samples by factors of 2, 3 and 4
        double[] half = downsample(samples, 2);
        double[] third = downsample(samples, 3);
        double[] quarter = downsample(samples, 4);

        // Multiply the downsampled arrays together
        double[] hps = new double[samples.length];
        for (int i = 0; i < hps.length - 1; i += 2) {

            double[] tmp = multiplyComplex(samples[i], samples[i + 1],
                    half[i], half[i + 1]);

            tmp = multiplyComplex(tmp[0], tmp[1], third[i], third[i + 1]);
            tmp = multiplyComplex(tmp[0], tmp[1], quarter[i], quarter[i + 1]);

            hps[i] = tmp[0];
            hps[i + 1] = tmp[1];
        }

        return hps;
    }

    /**
     * Downsamples a signal by the specified factor.
     */
    private double[] downsample(double[] original, int factor) {
        double[] downsampled = new double[original.length];

        // Keep only every factor'th value
        for (int i = 0; i < downsampled.length / factor; i += 2) {
            downsampled[i] = original[i * factor];
            downsampled[i + 1] = original[i * factor + 1];
        }

        return downsampled;
    }

    /**
     * Determines the index with the greatest magnitude.
     */
    private int getMaxIndex(double[] freqs) {
        int maxIndex;
        double currentMagnitude, maxMagnitude;

        maxIndex = 0;
        currentMagnitude = maxMagnitude = 0;

        for(int i = 1; i < freqs.length / 2; i++ ) {

            // Calculate the magnitude of the complex number
            currentMagnitude = Math.sqrt(freqs[i * 2] * freqs[i * 2] + 
                    freqs[i * 2 + 1] * freqs[i * 2 + 1]);

            // Compare current magnitude to max magnitude        	
            if(currentMagnitude > maxMagnitude) {
                maxIndex = i;
                maxMagnitude = currentMagnitude;
            }

        }
        return maxIndex;
    }

    /**
     * Multiplies two complex numbers together.
     */
    private double[] multiplyComplex(double r1, double i1, double r2, double i2) {
        double[] product = new double[2];
        product[0] = r1 * r2 - i1 * i2;
        product[1] = r1 * i2 + i1 * r2;
        return product;
    }    
}
