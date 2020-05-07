package obba;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	private static final String FRAME_TITLE = "Chat";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
	private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
	public String getTarget() {
		return target;
	}

	private static final int SMALL_GAP = 5;
	private static final int MEDIUM_GAP = 10;
	private static final int LARGE_GAP = 15;
	private static final int SERVER_PORT = 4567;
	private final JTextArea textAreaIncoming = new JTextArea(
			INCOMING_AREA_DEFAULT_ROWS, 0);
	private final JTextArea textAreaOutgoing = new JTextArea(
			OUTGOING_AREA_DEFAULT_ROWS, 0);
	private String username;
	private String password;
	private String target;
	public void setText(String text){
		textAreaIncoming.append(target + " -> " + text
				+ "\n");
	}

	Frame(String username, String password,String target) {
		super(FRAME_TITLE);
		this.username=username;
		this.password=password;
		this.target=target;

		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		// ������������� ����
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit
				.getScreenSize().height - getHeight()) / 2);
		// ��������� ������� ��� ����������� ���������� ���������
		textAreaIncoming.setEditable(false);
		// ���������, �������������� ��������� ��������� �������
		final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
		// ���� ����� ����� ������������ � ������ ����������

		// ��������� ������� ��� ����� ���������

		// ���������, �������������� ��������� ��������� �������
		final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
		// ������ ����� ���������
		final JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));
		// ������ �������� ���������
		final JButton sendButton = new JButton("���������");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		// ���������� ��������� ������ "���������"
		final GroupLayout layout2 = new GroupLayout(messagePanel);
		messagePanel.setLayout(layout2);
		layout2.setHorizontalGroup(layout2.createSequentialGroup()
				.addContainerGap().addGroup(
						layout2.createParallelGroup(Alignment.TRAILING)
								.addGroup(
										layout2.createSequentialGroup().addGap(
												LARGE_GAP)).addComponent(
										scrollPaneOutgoing).addComponent(
										sendButton)).addContainerGap());
		layout2.setVerticalGroup(layout2.createSequentialGroup()
				.addContainerGap().addGroup(
						layout2.createParallelGroup(Alignment.BASELINE))
				.addGap(MEDIUM_GAP).addComponent(scrollPaneOutgoing).addGap(MEDIUM_GAP)
						.addComponent(sendButton).addContainerGap());
		// ���������� ��������� ������
		final GroupLayout layout1 = new GroupLayout(getContentPane());
		setLayout(layout1);
		layout1.setHorizontalGroup(layout1.createSequentialGroup()
				.addContainerGap().addGroup(
						layout1.createParallelGroup().addComponent(
								scrollPaneIncoming).addComponent(messagePanel))
				.addContainerGap());
		layout1.setVerticalGroup(layout1.createSequentialGroup()
				.addContainerGap().addComponent(scrollPaneIncoming).addGap(
						MEDIUM_GAP).addComponent(messagePanel)
				.addContainerGap());
		/*
		 * if(!authorize(username,password)){ sendButton.setEnabled(false); }
		 */
	}

	private synchronized void sendMessage() {
		try {
			final String message = textAreaOutgoing.getText();
			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(this, "������� ����� ���������",
						"������", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// ������� ����� ��� ����������
			final Socket socket = new Socket("127.0.0.1", SERVER_PORT);

			// ��������� ����� ������ ������
			final DataOutputStream out = new DataOutputStream(socket
					.getOutputStream());
			// ���������� � ����� ���
			out.writeUTF(username);
			out.writeUTF(password);
			out.writeUTF("0");
			out.writeUTF(target);
			// ���������� � ����� ���������
			out.writeUTF(message);
			// �������� ��������� � ��������� ������� ������
			textAreaIncoming.append("� -> " + target + ": " + message
					+ "\n");
			// ������� ��������� ������� ����� ���������
			textAreaOutgoing.setText("");
			// ��������� �����

		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Frame.this,
					"�� ������� ��������� ���������: ����-������� �� ������",

					"������", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Frame.this,
					"�� ������� ��������� ���������", "������",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}