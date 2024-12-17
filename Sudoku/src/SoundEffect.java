import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundEffect {

    // Method to play a sound given the file path
    public void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to play the sound for a correct answer
    public void playCorrectSound() {
        playSound("correct.wav"); // Update with the correct path to your sound file
    }

    // Method to play the sound for a wrong answer
    public void playWrongSound() {
        playSound("wrong.wav"); // Update with the correct path to your sound file
    }
}