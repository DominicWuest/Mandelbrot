import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.Vector;
import static java.lang.Math.signum;

/*
*
*  This is a program with the intention of easily exploring the Mandelbrot Set
*  It uses multithreading to increase performance and make the navigation smoother
*
*/

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

  // Instance of the class itself because of eventlisteners
  static Mandelbrot mandelbrot = new Mandelbrot();

  // Matrix of displayed pixels
  static volatile int[][] display = new int[windowWidth][windowHeight];

  // Main JFrame
  static JFrame frame = new JFrame("Mandelbrot");

  // Maximum amount of times to call the Mandelbrot function
  // Until it exceeds the bound
  static int maxIterations = 500, bound = 2;

  // Amount of threads available in the current machine
  static final int N_THREADS = Runtime.getRuntime().availableProcessors();

  // Array of the threads calculating the Mandelbrot
  static Iterator[] iterators = new Iterator[N_THREADS - 2];

  // Object of ImportantPixelCalculator, it calculates the blurry pixels when navigating over the picture
  static ImportantPixelCalculator importantPixelCalculator = new ImportantPixelCalculator();;

  // Array of the threads' status (true = finished; false = calculating)
  static volatile boolean[] finishedStatus = new boolean[N_THREADS - 2];

  // Where everything gets painted on
  static BufferedImage canvas = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);

  // Last x- and y-position of the mouse so that mouseDragged knows how far it should move the picture
  static int lastMouseX, lastMouseY;

  // Last mouse button which has been pressed (-1 = none, 1 = left-click)
  static int lastButtonPressed = -1;

  // Start of variables used for ImportantPixelCalculator to know which ones to calculate
  // The start from where to calculate the important pixels
  static volatile int importantX = 0, importantY = 0;

  // In which direction was the window pulled (true = in the negative axis)
  static boolean xMax, yMax;
  // End of variables used for ImportantPixelCalculator to know which ones to calculate

  static JButton menuButton = new JButton();

  static boolean menu = false;

  // Main function
  public static void main(String[] args) {

    // Initializing frame and adding eventlisteners to it
    frame.setSize(windowWidth, windowHeight + 30);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.add(mandelbrot);
    frame.addMouseListener(mandelbrot);
    frame.addMouseMotionListener(mandelbrot);
    frame.addMouseWheelListener(mandelbrot);

    mandelbrot.setLayout(null);
    mandelbrot.add(menuButton);
    menuButton.setBounds(10, 10, 40, 40);
    menuButton.addMouseListener(new MouseListener() {
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
          menu = true;
          menuButton.setVisible(false);
        }
      }

      // Unused EventListener functions
      public void mouseClicked(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}
    });

    // Main Loop
    while (true) {
      if (menu) {

      } else {
        // Starts new threads and assigns them the coordinates to calculate
        for (int i = 0; i < iterators.length; i++) {
          iterators[i] = new Iterator(i, windowHeight / (N_THREADS - 2) * i, windowHeight / (N_THREADS - 2) * (i + 1));
          finishedStatus[i] = false;
        }
        // Checks if all threads have finished calculating
        while (true)  {
          boolean finishedChecker = true;
          for (int i = 0; i < iterators.length; i++) {
            if (!finishedStatus[i]) {
              finishedChecker = false;
              break;
            }
          }
          if (finishedChecker) break;
          // Repaints after every check to better see progress
          mandelbrot.repaint();
        }
        // Repaints after all threads have finished calculating
        mandelbrot.repaint();
      }
    }
  }

  // Function to paint everything needed
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (menu) {

    } else {
      Graphics2D g2 = (Graphics2D)g;
      // Draws every pixel of the display matrix
      for (int x = 0; x < windowWidth; x++) {
        for (int y = 0; y < windowHeight; y++) {
          // setRGB takes the color as a 24-bit integer, Alpha / Red / Green / Blue
          canvas.setRGB(x, y, (255 << 24) | (0 << 16) | (0 << 8) | display[x][y]);
        }
      }
      g2.drawImage(canvas, null, null);
    }
  }

  // Zooms into or out of the image
  // A negative zoomValue will cause it to zoom out, a positive to zoom in
  public void zoom(double zoomValue, MouseWheelEvent e) {
    // Mouse position in x-axis relative to window in percent
    double percentageLeft;
    // Mouse position in y-axis relative to window in percent
    double percentageTop;
    // It only zooms realtive to the mouses position if it zooms into the image
    if (zoomValue > 0) {
      percentageLeft = e.getX() / (double)(windowWidth);
      percentageTop = e.getY() / (double)(windowHeight);
    } else {
      percentageLeft = 0.5;
      percentageTop = 0.5;
    }
    // Amount to increase max-values in x-axis
    double dx = Math.abs(maxValues[LEFT] - maxValues[RIGHT]) / zoomValue;
    // Amount to increase max-values in y-axis
    double dy = Math.abs(maxValues[TOP] - maxValues[BOTTOM]) / zoomValue;
    maxValues[RIGHT] -= (1.0 - percentageLeft) * dx;
    maxValues[LEFT] += percentageLeft * dx;
    maxValues[BOTTOM] -= (1.0 - percentageTop) * dy;
    maxValues[TOP] += percentageTop * dy;
  }

  // Changes maxIterations (max amount of iterations in iterator-class)
  public void changeMaxIterations(double wheelValue) {
    // The smaller maxIterations, the more gradually it should de- and increase

    // Changes by 1 if below 10
    if (maxIterations  < 10) maxIterations += signum(wheelValue);
    // Changes by 2 if below 30
    else if (maxIterations < 30) maxIterations += 2 * signum(wheelValue);
    // Changes by 10 if above 30
    else maxIterations += 10 * signum(wheelValue);
    // Unnecessary to have maxIterations below 0, since picture won't change then
    if (maxIterations == -1) maxIterations = 0;
  }

  // Gets called when a mouseButton is pressed and mouse is moved
  // Navigates over picture
  public void mouseDragged(MouseEvent e) {
    // Checks if leftmouse button is pressed
    if (lastButtonPressed == 1) {
      // Change in mouse position in x-axis
      int dx = lastMouseX - e.getX();
      // Change in mouse position in y-axis
      int dy = lastMouseY - e.getY();
      // Change the following variables so the ImportantPixelCalculator prioritizes them over the usual pixels it would have to caluclate
      importantX = windowWidth - ((windowWidth + dx) % windowWidth);
      importantY = windowHeight - ((windowHeight + dy) % windowHeight);
      xMax = dx > 0 ? true : false;
      yMax = dy > 0 ? true : false;
      // Amount to increase maxValues in x-axis
      double toIncreaseX = (Math.abs(maxValues[LEFT] - maxValues[RIGHT])) * (dx / (double)windowWidth);
      // Amount to increase maxValues in y-axis
      double toIncreaseY = (Math.abs(maxValues[TOP] - maxValues[BOTTOM])) * (dy / (double)windowHeight);
      // Increase maxValues by the above declared values
      maxValues[RIGHT] += toIncreaseX;
      maxValues[LEFT] += toIncreaseX;
      maxValues[BOTTOM] += toIncreaseY;
      maxValues[TOP] += toIncreaseY;
      // Matrix to hold new image
      int[][] copy = new int[windowWidth][windowHeight];
      // Iterates over display-matrix to change copy-matrix
      for (int x = 0; x < windowWidth; x++) {
        // Checks if x + dx is inside array-bounds
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
      // Assing displayed pixels to newly generated image
      display = copy;
      // Assings last mouseposition to current one
      lastMouseX = e.getX();
      lastMouseY = e.getY();
    }
  }

/*
  *
  *  Gets called when mouse gets moved in any way
  *  Assings last mouseposition to current one
  *  Else it would reset to its original position when dragged
  *  @see Mandelbrot#mouseDragged(MouseEvent e)
  *
*/
  public void mouseMoved(MouseEvent e) {
    lastMouseX = e.getX();
    lastMouseY = e.getY();
  }

  // Gets called when scrolling
  // Zooms if scrolled or changes maxIterations when scrolled while holding ctrl
  public void mouseWheelMoved(MouseWheelEvent e) {
    switch (e.getModifiersEx()) {
      case 0: zoom(1 / e.getPreciseWheelRotation() * 5, e); break;
      case 128: changeMaxIterations(e.getPreciseWheelRotation()); break;
    }
  }

  // Gets called when a mousebutton gets pressed, assigning lastMousePressed to that button
  public void mousePressed(MouseEvent e) {
    lastButtonPressed = e.getButton();
  }

  // Gets called when a mousebutton gets released, meaning that no key is pressed
  public void mouseReleased(MouseEvent e) {
    lastButtonPressed = -1;
  }

  // Ununsed EventListener functions
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

}
