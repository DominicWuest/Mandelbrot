/*
 *
 * This class calculates the pixels which get blurry when navigating around the picture
 * This class runs on a seperate thread.
 *
*/

class ImportantPixelCalculator implements Runnable {

  // Object of the Thread on which the process is going to run on
  Thread myThread;

  // Constructor
  ImportantPixelCalculator() {
    // Initializing thread with the name of "ImportantPixelCalculator"
    myThread = new Thread(this, "ImportantPixelCalculator");
    // Starting new Thread
    myThread.start();
  }

  // This function runs in parallel and gets called after starting the thread
  public void run() {
    // Endless loop to check if there are pixels the class needs to calculate
    while (true) {
      if (Mandelbrot.importantX != 0 || Mandelbrot.importantY != 0) {
        calculateImportant(Mandelbrot.importantX, Mandelbrot.importantY, Mandelbrot.xMax, Mandelbrot.yMax);
      }
    }
  }

  // This function calculates all blurry pixels after navigating over the screen
  public void calculateImportant(int origX, int origY, boolean xMax, boolean yMax) {
    // Redeclare the variables to be 0, so that this function won't unnecessarily get called again
    Mandelbrot.importantX = 0;
    Mandelbrot.importantY = 0;

    // Checks if the user navigated over the picture in the x-Axis
    if (origX != 0 && origX != Mandelbrot.windowWidth) {
      // Start iterating from x = origX as long as x isn't out of bounds and decrement it if xMax == true, else increment
      for (int x = origX; x >= 0 && x < Mandelbrot.windowWidth; x += xMax ? -1 : 1) {
        // Map a (real part) to be between the maxValues for the x-axis
        double a = map(x, 0, Mandelbrot.windowWidth, Mandelbrot.maxValues[Mandelbrot.LEFT], Mandelbrot.maxValues[Mandelbrot.RIGHT]);
        for (int y = 0; y < Mandelbrot.windowHeight; y++) {
          // Map bi (imaginary part) to be between the maxValues for the y-axis
          double bi = map(y, 0, Mandelbrot.windowHeight, Mandelbrot.maxValues[Mandelbrot.TOP], Mandelbrot.maxValues[Mandelbrot.BOTTOM]);
          // Set the color of the calculated pixel
          setColor(x, y, iterationsNeeded(a, bi));
        }
      }
    }
    // Checks if the user navigated over the picture in the y-Axis
    if (origY != 0 && origY != Mandelbrot.windowHeight) {
      // Start iterating from y = origY as long as y isn't out of bounds and increment it if yMax == true, else decrement
      for (int y = origY; y >= 0 && y < Mandelbrot.windowHeight; y += yMax ? 1 : -1) {
        // Map bi (imaginary part) to be between the maxValues for the y-axis
        double bi = map(y, 0, Mandelbrot.windowHeight, Mandelbrot.maxValues[Mandelbrot.TOP], Mandelbrot.maxValues[Mandelbrot.BOTTOM]);
        // Start iterating from x = origX since the first loop calculated a part already
        for (int x = origX; x >= 0 && x < Mandelbrot.windowWidth; x += xMax ? -1 : 1) {
          // Map a (real part) to be between the maxValues for the x-axis
          double a = map(x, 0, Mandelbrot.windowWidth, Mandelbrot.maxValues[Mandelbrot.LEFT], Mandelbrot.maxValues[Mandelbrot.RIGHT]);
          // Set the color of the calculated pixel
          setColor(x, y, iterationsNeeded(a, bi));
        }
      }
    }
  }

  // This function returns the needed iterations until the function value gets bigger than the specified bounds (Mandelbrot.bound)
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
        // Needed iterations calculated, break the loop
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
