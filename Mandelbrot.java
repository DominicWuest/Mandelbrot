import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
class Mandelbrot extends JPanel {
  // Array of the maximum Values the points should have in the order of
  // Top, Right, Bottom, Left
  static double[] maxValues = {-1, 1, 1, -2.25};
  // Constants for the position of the maximum Values
  // In the array maxValues
  final static int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3;

  static final int windowWidth = 1000, windowHeight = 1000;

  static Mandelbrot mandelbrot;

  static JFrame frame = new JFrame("Mandelbrot");

  static int maxIterations = 500, bound = 2;

  static final int N_THREADS = Runtime.getRuntime().availableProcessors();

  static Iterator[] iterators = new Iterator[N_THREADS - 1];

  static boolean[] finishedStatus = new boolean[N_THREADS - 1];

  static Long time;

  static int iteratorIndex = 0;

  static boolean finishedChecker;

<<<<<<< HEAD
  public static BufferedImage canvas;
=======
  static BufferedImage canvas;
>>>>>>> f428ef8b7cc530338286339b29afd92e128ec15d

  public static void main(String[] args) {

    mandelbrot = new Mandelbrot();

    canvas = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);

    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);

    while (true) {
      time = System.nanoTime();
      for (int i = 0; i < iterators.length; i++) {
        iterators[i] = new Iterator(i, windowHeight / (N_THREADS - 1) * i, windowHeight / (N_THREADS - 1) * (i + 1));
        finishedStatus[i] = true;
      }
      while (true)  {
        finishedChecker = true;
        for (int i = 0; i < iterators.length; i++) {
          if (finishedStatus[i]) {
            finishedChecker = false;
            break;
          }
        }
        if (finishedChecker) break;
      }
      System.out.println((System.nanoTime() - time) / 1000000000.0 + " s");
      mandelbrot.repaint();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    g2.drawImage(canvas, null, null);
  }

}
