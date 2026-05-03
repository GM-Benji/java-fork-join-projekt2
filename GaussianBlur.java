import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GaussianBlur {

    // Jądro Gaussa 3x3
    private static final float[] KERNEL = {
        1/16f, 2/16f, 1/16f,
        2/16f, 4/16f, 2/16f,
        1/16f, 2/16f, 1/16f
    };

    public static void main(String[] args) {
        try {
            File inputFile = new File("assets/test_image.jpg");
            if (!inputFile.exists()) {
                System.out.println("Błąd: Nie znaleziono pliku input.jpg w katalogu głównym.");
                return;
            }

            BufferedImage sourceImage = ImageIO.read(inputFile);
            long startTime = System.currentTimeMillis();

            // URUCHOMIENIE SEKWENCYJNE (Baza do Fork-Join)
            BufferedImage resultImage = applyBlurSequential(sourceImage);

            long endTime = System.currentTimeMillis();
            
            ImageIO.write(resultImage, "jpg", new File("output.jpg"));
            System.out.println("Przetwarzanie zakończone!");
            System.out.println("Czas wykonania (sekwencyjnie): " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda sekwencyjna - to ją będziemy dzielić na pod-zadania w Frazie 2.
     */
    private static BufferedImage applyBlurSequential(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Iteracja po pikselach (z pominięciem krawędzi dla uproszczenia bazy)
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                
                float r = 0, g = 0, b = 0;
                int kIdx = 0;

                // Splot (Convolution)
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = src.getRGB(x + kx, y + ky);
                        float weight = KERNEL[kIdx++];
                        
                        r += ((rgb >> 16) & 0xFF) * weight;
                        g += ((rgb >> 8) & 0xFF) * weight;
                        b += (rgb & 0xFF) * weight;
                    }
                }

                int blurredRgb = ((int)r << 16) | ((int)g << 8) | (int)b;
                dst.setRGB(x, y, blurredRgb);
            }
        }
        return dst;
    }
}
