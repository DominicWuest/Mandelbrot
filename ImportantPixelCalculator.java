/*
 *
 * This class calculates the pixels which get blurry when navigating around the picture
 * This class runs on a seperate thread.
 *
*/

class ImportantPixelCalculator extends Iterator implements Runnable {

  // Object of the Thread on which the process is going to run on
  Thread myThread;

  // Constructor
  ImportantPixelCalculator() {
    super(-1, -1, -1);
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

}
