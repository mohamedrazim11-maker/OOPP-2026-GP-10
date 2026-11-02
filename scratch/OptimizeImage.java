import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class OptimizeImage {
    public static void main(String[] args) throws Exception {
        File orig = new File("uni_image.jpeg");
        BufferedImage img = ImageIO.read(orig);
        int targetW = 1440;
        int targetH = (int) ((double) img.getHeight() / img.getWidth() * targetW);

        BufferedImage resized = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(img, 0, 0, targetW, targetH, null);
        g2.dispose();

        // Overwrite orig and resources copies with optimized image
        ImageIO.write(resized, "jpg", new File("uni_image.jpeg"));
        ImageIO.write(resized, "jpg", new File("resources/images/uni_image.jpeg"));
        new File("resources/uni_image.jpeg").delete(); // remove duplicate in resources root

        System.out.println("Optimized successfully. New size: " + new File("uni_image.jpeg").length() + " bytes");
    }
}
