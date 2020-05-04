package obba;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Field extends JPanel {
	// Флаг приостановленности движения
	private boolean paused;
	private boolean PausedbyPress = true;
	// Динамический список скачущих мячей
	private ArrayList<BouncingBall> balls = new ArrayList<BouncingBall>(10);
	private long startOfPress;
	private Point2D.Double cursor = new Point2D.Double(0, 0);
	private Point2D.Double cursor_end = new Point2D.Double(0, 0);

	public void setCursor(double x, double y) {
		cursor.x = x;
		cursor.y = y;
	}

	public void setCursor_e(double x, double y) {
		cursor_end.x = x;
		cursor_end.y = y;
	}

	// Класс таймер отвечает за регулярную генерацию событий ActionEvent
	// При создании его экземпляра используется анонимный класс,
	// реализующий интерфейс ActionListener
	private Timer repaintTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			// Задача обработчика события ActionEvent - перерисовка окна
			repaint();
		}
	});

	// Конструктор класса BouncingBall
	public Field() {
		// Установить цвет заднего фона белым
		setBackground(Color.WHITE);
		addMouseListener(new MyMouseAdapter());
		// Запустить таймер
		repaintTimer.start();
	}

	// Унаследованный от JPanel метод перерисовки компонента
	public void paintComponent(Graphics g) {
		// Вызвать версию метода, унаследованную от предка
		super.paintComponent(g);
		Graphics2D canvas = (Graphics2D) g;
		// Последовательно запросить прорисовку от всех мячей из списка
		for (BouncingBall ball : balls) {
			ball.paint(canvas);
		}
	}

	// Метод добавления нового мяча в список

	// Метод синхронизированный, т.е. только один поток может
	// одновременно быть внутри
	public synchronized void pause() {
		// Включить режим паузы
		paused = true;
	}

	// Метод синхронизированный, т.е. только один поток может
	// одновременно быть внутри
	public synchronized void resume() {
		// Выключить режим паузы
		paused = false;
		// Будим все ожидающие продолжения потоки
		notifyAll();
	}

	// Синхронизированный метод проверки, может ли мяч двигаться
	// (не включен ли режим паузы?)
	public synchronized void canMove(BouncingBall ball)
			throws InterruptedException {
		if (paused) {
			// Если режим паузы включен, то поток, зашедший
			// внутрь данного метода, засыпает
			wait();
		}
	}

	public void addBall() {
		// Заключается в добавлении в список нового экземпляра BouncingBall
		// Всю инициализацию положения, скорости, размера, цвета
		// BouncingBall выполняет сам в конструкторе
		balls.add(new BouncingBall(this));
	}

	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			startOfPress = System.currentTimeMillis();
			setCursor(e.getX(), e.getY());
			if (!paused)
				pause();
			else
				PausedbyPress = false;
		}

		public void mouseReleased(MouseEvent e) {
			long time = System.currentTimeMillis() - startOfPress;
			// setCursor(e.getX(),e.getY());
			double X = e.getX();
			double Y = e.getY();
			for (BouncingBall ball : balls) {
				int rad = ball.getradius();
				double difx = cursor.x - ball.getX();
				double dify = cursor.y - ball.getY();
				if ((Math.abs(difx) < rad) && (Math.abs(dify) < rad)) {
					ball.setSpeedX((X - ball.getX())  / time);
					ball.setSpeedY((Y - ball.getY()) / time);
				}
			}
			if (PausedbyPress)
				resume();
			PausedbyPress = true;
		}
	}
}
