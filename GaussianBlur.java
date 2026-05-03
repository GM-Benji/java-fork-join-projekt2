import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;

public class GaussianBlur {

    // Jądro Gaussa 3x3
    private static final float[] KERNEL = {
            1 / 16f, 2 / 16f, 1 / 16f,
            2 / 16f, 4 / 16f, 2 / 16f,
            1 / 16f, 2 / 16f, 1 / 16f
    };

    public static void main(String[] args) {
        try {
            File inputFile = new File("assets/test_image.jpg");
            if (!inputFile.exists()) {
                System.out.println("Błąd: Nie znaleziono pliku " + inputFile.getPath());
                return;
            }

            BufferedImage sourceImage = ImageIO.read(inputFile);
            BufferedImage resultImage = null;

            // ==========================================
            // PRZEŁĄCZNIK ARCHITEKTURY
            // 0 = Tryb Sekwencyjny
            // 1 (lub dowolna inna) = Tryb Fork-Join
            // ==========================================
            int MODE = 0;

            long startTime = System.currentTimeMillis();

            if (MODE == 0) {
                System.out.println("Rozpoczynam przetwarzanie: TRYB SEKWENCYJNY...");
                resultImage = applyBlurSequential(sourceImage);
            } else {
                System.out.println("Rozpoczynam przetwarzanie: TRYB FORK-JOIN...");
                int width = sourceImage.getWidth();
                int height = sourceImage.getHeight();
                resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                ForkJoinPool pool = new ForkJoinPool();
                BlurTask mainTask = new BlurTask(sourceImage, resultImage, 1, height - 1, width);
                
                pool.invoke(mainTask);
                pool.shutdown();
            }

            long endTime = System.currentTimeMillis();

            ImageIO.write(resultImage, "jpg", new File("output.jpg"));
            System.out.println("Przetwarzanie zakończone!");
            System.out.println("Czas wykonania: " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda sekwencyjna (Klasyczna)
     */
    private static BufferedImage applyBlurSequential(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {

                float r = 0, g = 0, b = 0;
                int kIdx = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = src.getRGB(x + kx, y + ky);
                        float weight = KERNEL[kIdx++];

                        r += ((rgb >> 16) & 0xFF) * weight;
                        g += ((rgb >> 8) & 0xFF) * weight;
                        b += (rgb & 0xFF) * weight;
                    }
                }

                int blurredRgb = ((int) r << 16) | ((int) g << 8) | (int) b;
                dst.setRGB(x, y, blurredRgb);
            }
        }
        return dst;
    }

    /**
     * Wewnętrzna klasa zadania dla frameworka Fork-Join.
     */
    static class BlurTask extends RecursiveAction {

        // Próg podziału zadania - określa, ile maksymalnie wierszy przetworzy pojedynczy wątek.
        private static final int THRESHOLD = 100;

        private final BufferedImage src;
        private final BufferedImage dst;
        private final int startY;
        private final int endY;
        private final int width;

        public BlurTask(BufferedImage src, BufferedImage dst, int startY, int endY, int width) {
            this.src = src;
            this.dst = dst;
            this.startY = startY;
            this.endY = endY;
            this.width = width;
        }

        @Override
        protected void compute() {
            int length = endY - startY;

            if (length <= THRESHOLD) {
                computeDirectly();
            } else {
                int split = startY + (length / 2);
                BlurTask task1 = new BlurTask(src, dst, startY, split, width);
                BlurTask task2 = new BlurTask(src, dst, split, endY, width);
                invokeAll(task1, task2);
            }
        }

        private void computeDirectly() {
            for (int y = startY; y < endY; y++) {
                for (int x = 1; x < width - 1; x++) {

                    float r = 0, g = 0, b = 0;
                    int kIdx = 0;

                    for (int ky = -1; ky <= 1; ky++) {
                        for (int kx = -1; kx <= 1; kx++) {
                            int rgb = src.getRGB(x + kx, y + ky);
                            float weight = KERNEL[kIdx++];

                            r += ((rgb >> 16) & 0xFF) * weight;
                            g += ((rgb >> 8) & 0xFF) * weight;
                            b += (rgb & 0xFF) * weight;
                        }
                    }

                    int blurredRgb = ((int) r << 16) | ((int) g << 8) | (int) b;
                    dst.setRGB(x, y, blurredRgb);
                }
            }
        }
    }
}