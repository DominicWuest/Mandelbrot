class ImportantPixelCalculator implements Runnable {

  // Object of the Thread on which the process is going to run on
  Thread myThread;
  /*
   *
   * One Thread caulcates all pixels from the coordinates (0, minY)
   * to (windowWidth - 1, maxY)
   * Every thread gets assigned a minY and a maxY so it knows which
   * pixels it needs to calculate
   *
  */
  int minY, maxY;

  // Constructor
  ImportantPixelCalculator() {

    // Initializing thread with the name of the given index
    myThread = new Thread(this, "ImportantPixelCalculator");
    // Starting new Thread
    myThread.start();
  }

  // This function runs in parallel and gets called after starting the thread
  public void run() {

    while (true) {
      if (Mandelbrot.importantX != 0 || Mandelbrot.importantY != 0) {
        calculateImportant(Mandelbrot.importantX, Mandelbrot.importantY, Mandelbrot.xMax, Mandelbrot.yMax);
      }
    }
  }

  public void calculateImportant(int origX, int origY, boolean xMax, boolean yMax) {
    Mandelbrot.importantX = 0;
    Mandelbrot.importantY = 0;

    if (origX != 0 && origX != Mandelbrot.windowWidth) {
      for (int x = origX; x >= 0 && x < Mandelbrot.windowWidth; x += xMax ? -1 : 1) {
        double a = map(x, 0, Mandelbrot.windowWidth, Mandelbrot.maxValues[Mandelbrot.LEFT], Mandelbrot.maxValues[Mandelbrot.RIGHT]);
        for (int y = 0; y < Mandelbrot.windowHeight; y++) {
          double bi = map(y, 0, Mandelbrot.windowHeight, Mandelbrot.maxValues[Mandelbrot.TOP], Mandelbrot.maxValues[Mandelbrot.BOTTOM]);
          setColor(x, y, iterationsNeeded(a, bi));
        }
      }
    }
    if (origY != 0 && origY != Mandelbrot.windowHeight) {
      for (int y = origY; y >= 0 && y < Mandelbrot.windowHeight; y += yMax ? 1 : -1) {
        double bi = map(y, 0, Mandelbrot.windowHeight, Mandelbrot.maxValues[Mandelbrot.TOP], Mandelbrot.maxValues[Mandelbrot.BOTTOM]);
        for (int x = origX; x >= 0 && x < Mandelbrot.windowWidth; x += xMax ? -1 : 1) {
          double a = map(x, 0, Mandelbrot.windowWidth, Mandelbrot.maxValues[Mandelbrot.LEFT], Mandelbrot.maxValues[Mandelbrot.RIGHT]);
          setColor(x, y, iterationsNeeded(a, bi));
        }
      }
    }
  }

  public int iterationsNeeded(double a, double bi) {
    // Amount of iterations needed until the function-value was out of bounds (bigger than Mandelbrot.bound)
    // Gets incremented after every new recursive function call
    int iterations = 0;
    // Current a (real) and bi (imaginary) part of the complex number to calculate. Used for caluclation of function-value for current (x, y)
    double currA = 0, currBi = 0, tempA;
    // Imitating the recursive function call with a for-loop
    for (int i = 0; i <= Mandelbrot.maxIterations; i++) {
      tempA = currA;
      currA = Math.pow(currA, 2) - Math.pow(currBi, 2) + a;
      currBi = 2 * tempA * currBi + bi;
      // Checks if complex number is out of bounds
      if (Math.sqrt(Math.pow(currA, 2) + Math.pow(currBi, 2)) > Mandelbrot.bound) {
        // Save the iterations it took for the complex number to be out of bounds (amount of loop-iterations)
        iterations = i;
        // No need to calculate more, break the loop
        break;
      }
    }
    return iterations;
  }

  /*
   *
   * This function calculates the color (0- 255) by mapping the iterations until the function-value was out of bounds
   * Which is a value between 0 and maxIterations to a value between 0 and 1
   * Then mapping the square root of this mapped value to a value between 0 and 255
   *
   * Then, assinging the caluclated pixel in the display matrix to the caluclated color
   *
  */
  public void setColor(int x, int y, int iterations) {
    Mandelbrot.display[x][y] = (int)map(Math.sqrt(map(iterations, 0, Mandelbrot.maxIterations, 0, 1)), 0, 1, 0, 255);
  }

  // This function maps the parameter a, which is between firstMin and firstMax to a number between secondMin and secondMax
  // Used to map the complex number for a certain pixel from a coordinate
  public static double map(double a, double firstMin, double firstMax, double secondMin, double secondMax) {
    return ((a / (firstMax - firstMin)) * (secondMax - secondMin)) + secondMin;
  }

}
