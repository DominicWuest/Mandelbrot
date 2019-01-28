import java.awt.event.*;

class ButtonListener implements MouseListener {

  private int task;

  public static final int reset = 0, MENU = 1, START = 2, OPTIONS = 3, QUIT = 4;

  ButtonListener(int task) {
    this.task = task;
  }

  public void mousePressed(MouseEvent e) {
    if (e.getButton() == 1) {
      switch (task) {
        case reset: reset(); break;
        case MENU: menu(); break;
        case START: start(); break;
        case OPTIONS: options(); break;
        case QUIT: quit();
      }
    }
  }

  public void reset() {
    Mandelbrot.maxValues = new double[]{-1, 1, 1, -2.25};
    Mandelbrot.maxIterations = 500;
  }

  public void menu() {
    Mandelbrot.mode = Mandelbrot.MENU_SCREEN;
    Mandelbrot.menuButton.setVisible(false);
    Mandelbrot.resetButton.setVisible(false);
    Mandelbrot.quitButton.setVisible(false);
    Mandelbrot.startButton.setVisible(true);
    Mandelbrot.optionsButton.setVisible(true);
    Mandelbrot.menuQuitButton.setVisible(true);
  }

  public void start() {
    Mandelbrot.mode = Mandelbrot.DRAW_MANDELBROT;
    reset();
    Mandelbrot.menuButton.setVisible(true);
    Mandelbrot.resetButton.setVisible(true);
    Mandelbrot.quitButton.setVisible(true);
    Mandelbrot.startButton.setVisible(false);
    Mandelbrot.optionsButton.setVisible(false);
    Mandelbrot.menuQuitButton.setVisible(false);
  }

  public void options() {

  }

  public void quit() {
    System.exit(0);
  }

  public void mouseExited(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}

}
