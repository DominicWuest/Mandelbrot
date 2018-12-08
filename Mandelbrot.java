import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

@SuppressWarnings("serial")
class Mandelbrot extends JPanel {
  // Array of the maximum Values the points should have in the order of
  // Top, Right, Bottom, Left
  static double[] maxValues = {-1.5, 2, 1.5, -2};
  // Constants for the position of the maximum Values
  // In the array maxValues
  final static int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3;

  static int windowWidth = 300, windowHeight = 300;

  static Mandelbrot mandelbrot;

  static JFrame frame = new JFrame("Mandelbrot");

  static double a, bi;

  static int iterations, maxIterations = 100, bound = 2;

  public static void main(String[] args) {

    mandelbrot = new Mandelbrot();

    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);

    while (true) {
      mandelbrot.repaint();
      for (int x = 0; x < windowWidth; x++) {
        for (int y = 0; y < windowHeight; y++) {
          a = map(x, 0, windowWidth, maxValues[LEFT], maxValues[RIGHT]);
          bi = map(y, 0, windowHeight, maxValues[TOP], maxValues[BOTTOM]);
          iterations = iterate(a, bi);
        }
      }
    }
  }

  public void paitnComponent(Graphics g) {
    super.paintComponent(g);
  }

  public int iterate(double a, double bi) {
    int iterations = 0;
    double currA, currBi, tempA;
    for (int i = 0; i < maxIterations; i++) {
      tempA = currA;
      currA = Math.pow(currA, 2) - Math.pow(currBi, 2) + a;
      currBi = 2 * tempA * currB + bi;
      if (Math.sqrt(Math.pow(a, 2) + Math.pow(bi, 2)) > bound) {
        iterations = i;
        break;
      }
    }
    return iterations;
  }

  public double map() {
    return 0;
  }

}
