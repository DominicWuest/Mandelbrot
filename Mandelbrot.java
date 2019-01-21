import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.Vector;

@SuppressWarnings("serial")
class Mandelbrot extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

  // Array of the maximum Values the points should have in the order of
  // Top, Right, Bottom, Left
  static volatile double[] maxValues = {-1, 1, 1, -2.25};

  // Constants for the position of the maximum Values
  // In the array maxValues
  static final int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3;

  // Variables to hold how wide and tall the displayed window is
  static final int windowWidth = 1000, windowHeight = 1000;

  static Mandelbrot mandelbrot;

  static volatile int[][] display = new int[windowWidth][windowHeight];

  static JFrame frame = new JFrame("Mandelbrot");

  static int maxIterations = 500, bound = 2;

  static double referenceIterations = maxIterations;

  static final int N_THREADS = Runtime.getRuntime().availableProcessors();

  static Iterator[] iterators = new Iterator[N_THREADS - 1];

  static volatile boolean[] finishedStatus = new boolean[N_THREADS - 1];

  static Long time;

  static int iteratorIndex = 0;

  static boolean finishedChecker;

  static BufferedImage canvas;

  static int lastMouseX, lastMouseY;

  static int lastButtonPressed = -1;

  public static void main(String[] args) {

    mandelbrot = new Mandelbrot();

    canvas = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);

    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);
    frame.addMouseListener(mandelbrot);
    frame.addMouseMotionListener(mandelbrot);
    frame.addMouseWheelListener(mandelbrot);

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
        mandelbrot.repaint();
      }
      //System.out.println((System.nanoTime() - time) / 1000000000.0 + " s");
      mandelbrot.repaint();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    for (int x = 0; x < windowWidth; x++) {
      for (int y = 0; y < windowHeight; y++) {
        canvas.setRGB(x, y, (255 << 24) | (0 << 16) | (0 << 8) | display[x][y]);
      }
    }
    g2.drawImage(canvas, null, null);
  }

  // Only Values > 2 will cause a zoom
  public void zoom(double zoomValue) {
    double dx = Math.abs(maxValues[LEFT] - maxValues[RIGHT]) / zoomValue;
    double dy = Math.abs(maxValues[TOP] - maxValues[BOTTOM]) / zoomValue;
    maxValues[RIGHT] -= dx;
    maxValues[LEFT] += dx;
    maxValues[BOTTOM] -= dy;
    maxValues[TOP] += dy;
  }

  public void changeMaxIterations(double wheelValue) {
    // TODO
  }

  public void mouseDragged(MouseEvent e) {
    if (lastButtonPressed == 1) {
      // Change in mouse position in x-axis
      int dx = lastMouseX - e.getX();
      // Change in mouse position in y-axis
      int dy = lastMouseY - e.getY();
      double toIncreaseX = (Math.abs(maxValues[LEFT] - maxValues[RIGHT])) * (dx / (double)windowWidth);
      double toIncreaseY = (Math.abs(maxValues[TOP] - maxValues[BOTTOM])) * (dy / (double)windowHeight);
      maxValues[RIGHT] += toIncreaseX;
      maxValues[LEFT] += toIncreaseX;
      maxValues[BOTTOM] += toIncreaseY;
      maxValues[TOP] += toIncreaseY;
      int[][] copy = new int[windowWidth][windowHeight];
      for (int x = 0; x < windowWidth; x++) {
        if (x + dx >= 0 && x + dx < windowWidth) {
          for (int y = 0; y < windowHeight; y++) {
            if (y + dy < 0) copy[x][y] = display[x][0];
            else if (y + dy >= windowHeight) copy[x][y] = display[x][windowHeight - 1];
            else copy[x][y] = display[x + dx][y + dy];
          }
        } else {
          int index;
          if (x + dx < 0) index = 0;
          else index = windowWidth - 1;
          for (int y = 0; y < windowHeight; y++) copy[x][y] = display[index][y];
        }
      }
      display = copy;
      lastMouseX = e.getX();
      lastMouseY = e.getY();
    }
  }

  public void mouseMoved(MouseEvent e) {
    lastMouseX = e.getX();
    lastMouseY = e.getY();
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    switch (e.getModifiersEx()) {
      case 0: zoom(e.getPreciseWheelRotation() * -10); break;
      case 128: changeMaxIterations(e.getPreciseWheelRotation()); break;
    }
  }

  public void mousePressed(MouseEvent e) {
    lastButtonPressed = e.getButton();
  }

  public void mouseReleased(MouseEvent e) {
    lastButtonPressed = -1;
  }

  // Ununsed EventListener functions
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

}
