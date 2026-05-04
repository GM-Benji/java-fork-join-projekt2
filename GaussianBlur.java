import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;

public class GaussianBlur {

    // Ustawiamy promień rozmycia. Promień 10 oznacza macierz 21x21 (441 pikseli do sprawdzenia dla
    // KAŻDEGO piksela obrazu!)
    private static final int RADIUS = 10;
    private static final int KERNEL_SIZE = RADIUS * 2 + 1;
    private static final float[] KERNEL = new float[KERNEL_SIZE * KERNEL_SIZE];

    // Dynamiczna inicjalizacja jądra
    static {
        float weight = 1.0f / (KERNEL_SIZE * KERNEL_SIZE);
        for (int i = 0; i < KERNEL.length; i++) {
            KERNEL[i] = weight;
        }
    }

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
            int MODE = 1; // Od razu ustawione na Fork-Join

            long startTime = System.currentTimeMillis();

            if (MODE == 0) {
                System.out.println("Rozpoczynam przetwarzanie: TRYB SEKWENCYJNY...");
                resultImage = applyBlurSequential(sourceImage);
            } else {
                System.out.println("Rozpoczynam przetwarzanie: TRYB FORK-JOIN...");
                int width = sourceImage.getWidth();
                int height = sourceImage.getHeight();
                resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                @SuppressWarnings("resource")
                ForkJoinPool pool = new ForkJoinPool();
                // Marginesy ustawione na RADIUS, żeby nie wyjść poza tablicę
                BlurTask mainTask =
                        new BlurTask(sourceImage, resultImage, RADIUS, height - RADIUS, width);

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

        for (int y = RADIUS; y < h - RADIUS; y++) {
            for (int x = RADIUS; x < w - RADIUS; x++) {

                float r = 0, g = 0, b = 0;
                int kIdx = 0;

                for (int ky = -RADIUS; ky <= RADIUS; ky++) {
                    for (int kx = -RADIUS; kx <= RADIUS; kx++) {
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
                for (int x = RADIUS; x < width - RADIUS; x++) {

                    float r = 0, g = 0, b = 0;
                    int kIdx = 0;

                    for (int ky = -RADIUS; ky <= RADIUS; ky++) {
                        for (int kx = -RADIUS; kx <= RADIUS; kx++) {
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
