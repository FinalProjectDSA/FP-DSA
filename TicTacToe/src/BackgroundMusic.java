import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class BackgroundMusic {

    private Clip clip;

    public BackgroundMusic(String resourcePath) {
        try {
            // Load the audio file using getResource
            URL audioFileUrl = getClass().getClassLoader().getResource("audio/bgm2.wav");

            if (audioFileUrl == null) {
                throw new IOException("Audio file not found: " + resourcePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFileUrl);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + e.getMessage());
        }
    }

    public void play() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
