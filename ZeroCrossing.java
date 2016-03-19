public class ZeroCrossing {

    public static double calculateZeroCrossings(float[] samples) {

        float count = 0;  // The number of zero-crossings in audioBytes

        for (int i = 1; i < samples.length; i++) {
            if (samples[i] < 0 && samples[i - 1] > 0)
                count++;
        }
        
        return count * samples.length / STuner.sampleRate;
    }
}
