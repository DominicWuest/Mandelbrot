import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

@SuppressWarnings("serial")
class Mandelbrot extends JPanel {
  // Array of the maximum Values the points should have in the order of
  // Top, Right, Bottom, Left
  static double[] maxValues = {-1, 1, 1, -2.25};
  // Constants for the position of the maximum Values
  // In the array maxValues
  final static int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3;

  static int windowWidth = 1000, windowHeight = 1000;

  static Mandelbrot mandelbrot;

  static JFrame frame = new JFrame("Mandelbrot");

  static int maxIterations = 500, bound = 2;

  static final int N_THREADS = Runtime.getRuntime().availableProcessors();

  static Iterator[] iterators = new Iterator[N_THREADS - 1];

  static Long time;

  static int iteratorIndex = 0;

  static Color[][] pixels = new Color[windowWidth][windowHeight];

  static boolean finishedChecker;

  public static void main(String[] args) {

    mandelbrot = new Mandelbrot();

    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);

    while (true) {
      time = System.nanoTime();
      for (int i = 0; i < iterators.length; i++) iterators[i] = new Iterator(i, windowHeight / (N_THREADS - 1) * i, windowHeight / (N_THREADS - 1) * (i + 1));
      while (true)  {
        finishedChecker = true;
        for (int i = 0; i < iterators.length; i++) {
          if (!iterators[i].finished) {
            finishedChecker = false;
            break;
          }
        }
        if (finishedChecker) break;
      }
      System.out.println((System.nanoTime() - time) / 1000000000.0);
      mandelbrot.repaint();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.setBackground(Color.WHITE);
    for (int x = 0; x < windowWidth; x++) {
      for (int y = 0; y < windowHeight; y++) {
        g.setColor(pixels[x][y]);
        g.drawLine(x, y, x, y);
      }
    }
  }

}
