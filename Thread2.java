import java.awt.Color;

class Thread2 extends Thread {

  double a, bi;
  int x, y;

  Thread2(double a, double bi) {
    this.a = a;
    this.bi = bi;
  }

  public void run() {
    int iterations = -1;
    double currA = a, currBi = bi, tempA;
    for (int i = 0; i < Mandelbrot.maxIterations; i++) {
      tempA = currA;
      currA = Math.pow(currA, 2) - Math.pow(currBi, 2) + this.a;
      currBi = 2 * tempA * currBi + this.bi;
      if (Math.sqrt(Math.pow(currA, 2) + Math.pow(currBi, 2)) > Mandelbrot.bound) {
        iterations = i;
        break;
      }
    }
    if (iterations == -1) Mandelbrot.pixels[this.x][this.y] = new Color(0);
    else Mandelbrot.pixels[this.x][this.y] = new Color(255);
  }

}
