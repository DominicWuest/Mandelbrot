import java.awt.Color;

class Iterator implements Runnable {

  Thread myThread;
  double a, bi;
  int minY, maxY;
  int iterations;
  double currA, currBi, tempA;
  int color;
  int index;

  Iterator(int index, int minY, int maxY) {
    myThread = new Thread(this, Integer.toString(index));
    this.index = index;
    this.minY = minY;
    this.maxY = maxY;
    myThread.start();
  }

  public void run() {
    for (int y = this.minY; y < this.maxY; y++) {
      bi = map(y, 0, Mandelbrot.windowHeight, Mandelbrot.maxValues[Mandelbrot.TOP], Mandelbrot.maxValues[Mandelbrot.BOTTOM]);
      for (int x = 0; x < Mandelbrot.windowWidth; x++) {
        this.iterations = 0;
        a = map(x, 0, Mandelbrot.windowWidth, Mandelbrot.maxValues[Mandelbrot.LEFT], Mandelbrot.maxValues[Mandelbrot.RIGHT]);
        currA = 0;
        currBi = 0;
        for (int i = 0; i <= Mandelbrot.maxIterations; i++) {
          tempA = currA;
          currA = Math.pow(currA, 2) - Math.pow(currBi, 2) + a;
          currBi = 2 * tempA * currBi + bi;
          if (Math.sqrt(Math.pow(currA, 2) + Math.pow(currBi, 2)) > Mandelbrot.bound) {
            iterations = i;
            break;
          }
        }
        color = (int)map(Math.sqrt(map(iterations, 0, Mandelbrot.maxIterations, 0, 1)), 0, 1, 0, 255);
        Mandelbrot.display[x][y] = color;
      }
    }
    Mandelbrot.finishedStatus[this.index] = false;
  }

  public double map(double a, double firstMin, double firstMax, double secondMin, double secondMax) {
    return ((a / (firstMax - firstMin)) * (secondMax - secondMin)) + secondMin;
  }

}
