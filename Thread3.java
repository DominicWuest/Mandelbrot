import java.awt.Color;

class Thread3 extends Thread {

  double a, bi;
  int x, y;

  Thread3(double a, double bi) {
    this.a = a;
    this.bi = bi;
  }

  public void run() {
    int iterations = -1;
    double currA = this.a, currBi = this.bi, tempA;
    if (Math.sqrt(Math.pow(currA, 2) + Math.pow(currBi, 2)) > Mandelbrot.bound) iterations = 0;
    else {
      for (int i = 1; i < Mandelbrot.maxIterations; i++) {
        tempA = currA;
        currA = Math.pow(currA, 2) - Math.pow(currBi, 2) + this.a;
        currBi = 2 * tempA * currBi + this.bi;
        if (Math.sqrt(Math.pow(currA, 2) + Math.pow(currBi, 2)) > Mandelbrot.bound) {
          iterations = i;
          break;
        }
      }
    }
    Mandelbrot.pixels[this.x][this.y] = new Color((int)Mandelbrot.map(Math.sqrt(Mandelbrot.map(iterations, 0, Mandelbrot.maxIterations, 0, 1)), 0, 1, 0, 255));
  }

}
