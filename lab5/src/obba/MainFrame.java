package obba;

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private JFileChooser fileChooser = null;

	private JCheckBoxMenuItem showAxisMenuItem;
	private JCheckBoxMenuItem showMarkersMenuItem;
	private JCheckBoxMenuItem showAreaMenuItem;
	private JCheckBoxMenuItem rotatedMenuItem;
	private JMenuItem saveMenuItem;

	private GraphicsDisplay display = new GraphicsDisplay();

	private boolean fileLoaded = false;

	public MainFrame() {

		super("���������� �������� ������� �� ������ ������� �������������� ������");

		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation(kit.getScreenSize().width / 2, 0);
		//setExtendedState(MAXIMIZED_BOTH);

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
		
		Action saveGraphicsAction = new AbstractAction(
		"��������� ���� � ��������") {
	public void actionPerformed(ActionEvent event) {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
		}
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
			display.saveToGraphicsFile(fileChooser.getSelectedFile());
	}
};
		saveMenuItem=new JMenuItem(saveGraphicsAction);
		saveMenuItem.setEnabled(fileLoaded);
		fileMenu.add(saveMenuItem);
		
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
				//saveMenuItem.setEnabled(true);
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