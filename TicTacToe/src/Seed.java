import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * This enum is used by:
 * 1. Player: takes value of CROSS or NOUGHT
 * 2. Cell content: takes value of CROSS, NOUGHT, or NO_SEED.
 *
 * We also attach a display image icon (text or image) for the items.
 * To draw the image:
 *   g.drawImage(content.getImage(), x, y, width, height, null);
 *
 * Ideally, we should define two enums with inheritance, which is,
 * however, not supported.
 */
public enum Seed {
    CROSS("X", "image/webe.gif"),
    NOUGHT("O", "image/tiffy.gif"),
    NO_SEED(" ", null);

    // Private variables
    private String displayName;
    private String imageFileName;
    private Image img = null;

    // Constructor (must be private)
    private Seed(String name, String imageFilename) {
        this.displayName = name;
        this.imageFileName = imageFilename;

        // Only load the image if a filename is provided
        if (imageFilename != null) {
            loadImage(imageFilename);
        }
    }

    // Method to load the image
    private void loadImage(String imageFilename) {
        URL imgURL = getClass().getClassLoader().getResource(imageFilename);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            this.img = icon.getImage();
        } else {
            System.err.println("Couldn't find file " + imageFilename);
        }
    }

    // Public getters
    public String getDisplayName() {
        return displayName;
    }

    public Image getImage() {
        return img;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    // Public setters to modify the display name and image
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
        loadImage(imageFileName); // Reload the image whenever the filename changes
    }
}
