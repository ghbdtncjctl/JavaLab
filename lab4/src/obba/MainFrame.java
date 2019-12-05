package obba;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private JFileChooser fileChooser = null;

	private JCheckBoxMenuItem showAxisMenuItem;
	private JCheckBoxMenuItem showMarkersMenuItem;
	private JCheckBoxMenuItem showAreaMenuItem;
	private JCheckBoxMenuItem rotatedMenuItem;

	private GraphicsDisplay display = new GraphicsDisplay();

	private boolean fileLoaded = false;

	public MainFrame() {

		super("���������� �������� ������� �� ������ ������� �������������� ������");

		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit
				.getScreenSize().height - HEIGHT) / 2);
		setExtendedState(MAXIMIZED_BOTH);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("����");
		menuBar.add(fileMenu);

		Action openGraphicsAction = new AbstractAction(
				"������� ���� � ��������") {
			public void actionPerformed(ActionEvent event) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
					openGraphics(fileChooser.getSelectedFile());
			}
		};

		fileMenu.add(openGraphicsAction);

		JMenu graphicsMenu = new JMenu("������");
		menuBar.add(graphicsMenu);

		Action showAxisAction = new AbstractAction("���������� ��� ���������") {
			public void actionPerformed(ActionEvent event) {
				display.setShowAxis(showAxisMenuItem.isSelected());
			}
		};
		showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
		graphicsMenu.add(showAxisMenuItem);
		showAxisMenuItem.setSelected(true);

		Action showMarkersAction = new AbstractAction(
				"���������� ������� �����") {
			public void actionPerformed(ActionEvent event) {
				display.setShowMarkers(showMarkersMenuItem.isSelected());
			}
		};
		showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
		showMarkersMenuItem.setSelected(true);
		graphicsMenu.add(showMarkersMenuItem);
		graphicsMenu.addMenuListener(new GraphicsMenuListener());
		
		Action showAreaAction = new AbstractAction("���������� ����������� �������") {
			public void actionPerformed(ActionEvent event) {
				display.setShowArea(showAreaMenuItem.isSelected());
			}
		};
		showAreaMenuItem = new JCheckBoxMenuItem(showAreaAction);
		graphicsMenu.add(showAreaMenuItem);
		showAreaMenuItem.setSelected(false);
		
		Action rotateAction = new AbstractAction("��������� ������") {
			public void actionPerformed(ActionEvent event) {
				display.setRotated(rotatedMenuItem.isSelected());
			}
		};
		rotatedMenuItem = new JCheckBoxMenuItem(rotateAction);
		graphicsMenu.add(rotatedMenuItem);
		rotatedMenuItem.setSelected(false);
		
		getContentPane().add(display, BorderLayout.CENTER);
	}

	protected void openGraphics(File selectedFile) {
		try {

			DataInputStream in = new DataInputStream(new FileInputStream(
					selectedFile));
			Double[][] graphicsData = new Double[in.available()
					/ (Double.SIZE / 8) / 2][];
			int i = 0;
			while (in.available() > 0) {
				Double x = in.readDouble();
				Double y = in.readDouble();
				graphicsData[i++] = new Double[] { x, y };
			}

			if (graphicsData != null && graphicsData.length > 0) {
				fileLoaded = true;
				display.showGraphics(graphicsData);
			}
			in.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(MainFrame.this,
					"��������� ���� �� ������", "������ �������� ������",
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(MainFrame.this,
					"������ ������ ��������� ����� �� �����",
					"������ �������� ������", JOptionPane.WARNING_MESSAGE);
			return;
		}
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private class GraphicsMenuListener implements MenuListener {
		public void menuSelected(MenuEvent e) {
			showAxisMenuItem.setEnabled(fileLoaded);
			showMarkersMenuItem.setEnabled(fileLoaded);
			showAreaMenuItem.setEnabled(fileLoaded);
			rotatedMenuItem.setEnabled(fileLoaded);
		}

		public void menuDeselected(MenuEvent e) {
		}
		public void menuCanceled(MenuEvent e) {
		}
	}
}