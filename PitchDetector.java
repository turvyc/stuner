import org.jtransforms.fft.DoubleFFT_1D;

public class PitchDetector {
		
    public double getPitch(byte[] audioBytes) {
    	
        // Convert the byte array into the actual sample values
        double[] samples = convToDouble(audioBytes);
                        
        // Use FFT to convert from time domain to frequency domain
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);        
        fft.realForward(samples);

        // Determine the harmonic product spectrum
        double[] hps = getHarmonicProductSpectrum(samples);

        // Determine the index of the frequency with the maximum magnitude
        int maxIndex = getMaxIndex(hps);

        // Calculate and return the frequency
        System.out.println("Pitch: " + STuner.SAMPLE_RATE * maxIndex / samples.length);
        return STuner.SAMPLE_RATE * maxIndex / samples.length;           
    }

    private double[] getHarmonicProductSpectrum(double[] samples) {
        // Downsample the original samples by factors of 2, 3 and 4
        double[] half = downsample(samples, 2);
        double[] third = downsample(samples, 3);
        double[] ft = downsample(samples, 5);

        // Multiply the downsampled arrays together
        double[] hps = new double[samples.length];
        for (int i = 0; i < hps.length - 1; i += 2) {
            
            double[] tmp = multiplyComplex(samples[i], samples[i + 1],
                    half[i], half[i + 1]);
            
            tmp = multiplyComplex(tmp[0], tmp[1], third[i], third[i + 1]);
            tmp = multiplyComplex(tmp[0], tmp[1], ft[i], ft[i + 1]);
            
            hps[i] = tmp[0];
            hps[i + 1] = tmp[1];
        }

        return hps;
    }

    private double[] downsample(double[] original, int factor) {
        double[] downsampled = new double[original.length];

        for (int i = 0; i < downsampled.length / factor; i += 2) {
            downsampled[i] = original[i * factor];
            downsampled[i + 1] = original[i * factor + 1];
        }

        return downsampled;
    }

    private int getMaxIndex(double[] freqs) {
        int maxIndex, secondIndex, thirdIndex;
        double currentMagnitude, maxMagnitude, secondMagnitude, thirdMagnitude;

        maxIndex = secondIndex = thirdIndex = 0;
        currentMagnitude = maxMagnitude = secondMagnitude = thirdMagnitude = 0;

        for(int i = 1; i < freqs.length / 2; i++ ) {

            // Calculate the magnitude of the complex number
        	currentMagnitude = Math.sqrt(freqs[i * 2] * freqs[i * 2] + 
                    freqs[i * 2 + 1] * freqs[i * 2 + 1]);
        	
        	// Compare current magnitude to max magnitude        	
            if(currentMagnitude > maxMagnitude) {
                thirdIndex = secondIndex;
                thirdMagnitude = secondMagnitude;
                secondIndex = maxIndex;
                secondMagnitude = maxMagnitude;
                maxIndex = i;
                maxMagnitude = currentMagnitude;
            }

            else if (currentMagnitude > secondMagnitude) {
                thirdIndex = secondIndex;
                thirdMagnitude = secondMagnitude;
                secondIndex = i;
                secondMagnitude = currentMagnitude;
            }

            else if (currentMagnitude > thirdMagnitude) {
                thirdIndex = i;
                thirdMagnitude = currentMagnitude;
            }
        }
        return Math.min(maxIndex, Math.min(secondIndex, thirdIndex));
    }

    private double[] convToDouble(byte[] byteArr) {
        double[] doubleArr = new double[byteArr.length / 2];
        for (int i = 0; i < doubleArr.length; i++)
            doubleArr[i] = (double) (byteArr[2 * i + 1] << 8 | byteArr[2 * i]);
        return doubleArr;
    }

    private double[] multiplyComplex(double r1, double i1, double r2, double i2) {
        double[] product = new double[2];
        product[0] = r1 * r2 - i1 * i2;
        product[1] = r1 * i2 + i1 * r2;
        return product;
    }    
}
