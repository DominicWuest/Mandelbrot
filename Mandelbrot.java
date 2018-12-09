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

  static double a, bi;

  static int iterations, maxIterations = 500, bound = 2;

  static Thread1 thread1 = new Thread1(a, bi);
  static Thread2 thread2 = new Thread2(a, bi);
  static Thread3 thread3 = new Thread3(a, bi);

  static Long time;

  static int iterator = 0;

  static Color[][] pixels = new Color[windowWidth][windowHeight];

  public static void main(String[] args) {

    mandelbrot = new Mandelbrot();

    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);

    while (true) {
      time = System.nanoTime();
      for (int x = 0; x < windowWidth; x++) {
        for (int y = 0; y < windowHeight; y++) {
          a = map(x, 0, windowWidth, maxValues[LEFT], maxValues[RIGHT]);
          bi = map(y, 0, windowHeight, maxValues[TOP], maxValues[BOTTOM]);
          iterator %= 3;
          switch (iterator) {
            case 0: thread1.a = a;
                    thread1.bi = bi;
                    thread1.x = x;
                    thread1.y = y;
                    thread1.run();
                    break;
            case 1: thread2.a = a;
                    thread2.bi = bi;
                    thread2.x = x;
                    thread2.y = y;
                    thread2.run();
                    break;
            case 2: thread3.a = a;
                    thread3.bi = bi;
                    thread3.x = x;
                    thread3.y = y;
                    thread3.run();
                    break;
          }
          iterator++;
          mandelbrot.repaint();
        }
      }
      System.out.println((System.nanoTime() - time) / 1000000000.0);
      iterator = 0;
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

  public static double map(double a, double firstMin, double firstMax, double secondMin, double secondMax) {
    return ((a / (firstMax - firstMin)) * (secondMax - secondMin)) + secondMin;
  }

}
