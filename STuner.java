import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class STuner {

    private static int bufferSize = 512;
    private static int bytesPerFrame = 1;

    public static void main(String args[]) {
        TunerFrame frame = new TunerFrame();
        frame.setVisible(true);
        PitchDetector detector = new PitchDetector();
        TargetDataLine microphone = getMicrophone();

        byte[] audioBytes = new byte[bufferSize * bytesPerFrame];
        microphone.start();

        while (true) {
            microphone.read(audioBytes, 0, audioBytes.length);
            float pitch = detector.getPitch(audioBytes);
            // compare
            // update GUI
            System.out.println(pitch);
        }
    }

    private static TargetDataLine getMicrophone() {
        float sampleRate = 8000.0f;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);                      
        TargetDataLine mic = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
        
        if (!AudioSystem.isLineSupported(info)) {
            System.out.print("\nAudio Line not supported");
            System.exit(-1);
        }

        // Open the microphone
        try {
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);
            
        } catch (LineUnavailableException ex) {
            System.out.print("\nLine Unavailable: " + ex + "\n");
            System.exit(-2);
        }

        return mic;
    }
}
