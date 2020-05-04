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
	// ���� ������������������ ��������
	private boolean paused;
	private boolean PausedbyPress = true;
	// ������������ ������ �������� �����
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

	// ����� ������ �������� �� ���������� ��������� ������� ActionEvent
	// ��� �������� ��� ���������� ������������ ��������� �����,
	// ����������� ��������� ActionListener
	private Timer repaintTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			// ������ ����������� ������� ActionEvent - ����������� ����
			repaint();
		}
	});

	// ����������� ������ BouncingBall
	public Field() {
		// ���������� ���� ������� ���� �����
		setBackground(Color.WHITE);
		addMouseListener(new MyMouseAdapter());
		// ��������� ������
		repaintTimer.start();
	}

	// �������������� �� JPanel ����� ����������� ����������
	public void paintComponent(Graphics g) {
		// ������� ������ ������, �������������� �� ������
		super.paintComponent(g);
		Graphics2D canvas = (Graphics2D) g;
		// ��������������� ��������� ���������� �� ���� ����� �� ������
		for (BouncingBall ball : balls) {
			ball.paint(canvas);
		}
	}

	// ����� ���������� ������ ���� � ������

	// ����� ������������������, �.�. ������ ���� ����� �����
	// ������������ ���� ������
	public synchronized void pause() {
		// �������� ����� �����
		paused = true;
	}

	// ����� ������������������, �.�. ������ ���� ����� �����
	// ������������ ���� ������
	public synchronized void resume() {
		// ��������� ����� �����
		paused = false;
		// ����� ��� ��������� ����������� ������
		notifyAll();
	}

	// ������������������ ����� ��������, ����� �� ��� ���������
	// (�� ������� �� ����� �����?)
	public synchronized void canMove(BouncingBall ball)
			throws InterruptedException {
		if (paused) {
			// ���� ����� ����� �������, �� �����, ��������
			// ������ ������� ������, ��������
			wait();
		}
	}

	public void addBall() {
		// ����������� � ���������� � ������ ������ ���������� BouncingBall
		// ��� ������������� ���������, ��������, �������, �����
		// BouncingBall ��������� ��� � ������������
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
