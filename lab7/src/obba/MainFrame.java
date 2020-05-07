package obba;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
	private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
	private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
	private static final int SMALL_GAP = 5;
	private static final int MEDIUM_GAP = 10;
	private static final int LARGE_GAP = 15;
	private static final int SERVER_PORT = 4567;
	private final JTextField textFieldTo = new JTextField(
			TO_FIELD_DEFAULT_COLUMNS);
	private final JTextArea textAreaIncoming = new JTextArea(
			INCOMING_AREA_DEFAULT_ROWS, 0);
	private final JTextArea textAreaOutgoing = new JTextArea(
			OUTGOING_AREA_DEFAULT_ROWS, 0);
	private static int PEER_PORT;
	Thread running;
	String username;
	String password;
	private boolean isCreated = false;
	String sender;

	public void setSender(String sender) {
		this.sender = sender;
	}

	NewFrame newframe;

	public MainFrame(int port, String c) {
		super(FRAME_TITLE + c);
		PEER_PORT = port;
		username = JOptionPane.showInputDialog("username");
		password = JOptionPane.showInputDialog("password");
		if (username == null || password == null)
			return;

		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		// Центрирование окна
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit
				.getScreenSize().height - getHeight()) / 2);
		// Текстовая область для отображения полученных сообщений
		textAreaIncoming.setEditable(false);
		// Контейнер, обеспечивающий прокрутку текстовой области
		final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
		// Подписи полей
		final JLabel labelTo = new JLabel("Получатель");
		// Поля ввода имени пользователя и адреса получателя

		// Текстовая область для ввода сообщения

		// Контейнер, обеспечивающий прокрутку текстовой области
		final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
		// Панель ввода сообщения
		final JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
		// Кнопка отправки сообщения
		final JButton sendButton = new JButton("Отправить");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		final JButton findButton = new JButton("find");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isCreated) {
					isCreated = true;
					newframe = new NewFrame(username, password);
					newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					newframe.setVisible(true);
				}

			}
		});
		// Компоновка элементов панели "Сообщение"
		final GroupLayout layout2 = new GroupLayout(messagePanel);
		messagePanel.setLayout(layout2);
		layout2.setHorizontalGroup(layout2.createSequentialGroup()
				.addContainerGap().addGroup(
						layout2.createParallelGroup(Alignment.TRAILING)
								.addGroup(
										layout2.createSequentialGroup().addGap(
												LARGE_GAP).addComponent(
												findButton).addGap(SMALL_GAP)
												.addComponent(labelTo).addGap(
														SMALL_GAP)
												.addComponent(textFieldTo))
								.addComponent(scrollPaneOutgoing).addComponent(
										sendButton)).addContainerGap());
		layout2.setVerticalGroup(layout2.createSequentialGroup()
				.addContainerGap().addGroup(
						layout2.createParallelGroup(Alignment.BASELINE)
								.addComponent(findButton).addComponent(labelTo)
								.addComponent(textFieldTo)).addGap(MEDIUM_GAP)
				.addComponent(scrollPaneOutgoing).addGap(MEDIUM_GAP)
				.addComponent(sendButton).addContainerGap());
		// Компоновка элементов фрейма
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
		// Создание и запуск потока-обработчика запросов
		running = new Thread(new Runnable() {
			public void run() {
				try {
					final ServerSocket serverSocket = new ServerSocket(
							PEER_PORT);

					while (!Thread.interrupted()) {
						final Socket socket = serverSocket.accept();
						// System.out.println(socket.getLocalSocketAddress());
						final DataInputStream in = new DataInputStream(socket
								.getInputStream());
						// Читаем имя отправителя
						final String senderName = in.readUTF();
						if (isCreated && newframe.Created()) {
							sender = newframe.getFrame().getTarget();
						}
						if (!senderName.equals(sender)) {
							// Читаем сообщение
							final String message = in.readUTF();
							// Выводим сообщение в текстовую область
							textAreaIncoming.append(senderName + " -> "
									+ message + "\n");
						} else {
							// Читаем сообщение
							final String message = in.readUTF();
							// Закрываем соединение

							newframe.getFrame().setText(message);
						}
						socket.close();
					}
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.this,
							"Ошибка в работе сервака", "Ошибка",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		running.start();
		if (!authorize(username, password)) {
			sendButton.setEnabled(false);
			findButton.setEnabled(false);
			running.interrupt();
			this.dispose();
		}
	}

	private synchronized boolean authorize(String un, String pw) {
		try { // Создаем сокет для соединения
			final Socket socket = new Socket("127.0.0.1", SERVER_PORT); // Открываем
			// поток
			// вывода
			// данных
			final DataOutputStream out = new DataOutputStream(socket
					.getOutputStream()); // Записываем в потокимя
			out.writeUTF(un);
			// Записываем в поток сообщение
			out.writeUTF(pw);
			out.writeUTF("0");
			out.writeUTF(un);
			out.writeUTF("testmessage");
			final DataInputStream in = new DataInputStream(socket
					.getInputStream());
			final String aut = in.readUTF();
			socket.close();
			if (aut.equals("1"))
				return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.this,
					"Не удалось отправить сообщение: узел-адресат не найден",

					"Ошибка", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.this,
					"Не удалось отправить сообщение", "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private synchronized void sendMessage() {
		try {
			// Получаем необходимые параметры
			final String targetName = textFieldTo.getText();
			final String message = textAreaOutgoing.getText();
			// Убеждаемся, что поля не пустые
			if (targetName.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Введите Name узла-получателя", "Ошибка",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Введите текст сообщения",
						"Ошибка", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Создаем сокет для соединения
			final Socket socket = new Socket("127.0.0.1", SERVER_PORT);

			// Открываем поток вывода данных
			final DataOutputStream out = new DataOutputStream(socket
					.getOutputStream());
			// Записываем в поток имя
			out.writeUTF(username);
			out.writeUTF(password);
			out.writeUTF("0");
			out.writeUTF(targetName);
			// Записываем в поток сообщение
			out.writeUTF(message);
			// Помещаем сообщения в текстовую область вывода
			textAreaIncoming.append("Я -> " + targetName + ": " + message
					+ "\n");
			// Очищаем текстовую область ввода сообщения
			textAreaOutgoing.setText("");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.this,
					"Не удалось отправить сообщение: узел-адресат не найден",

					"Ошибка", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.this,
					"Не удалось отправить сообщение", "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		new Server().start();
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final MainFrame frame = new MainFrame(4566, "a");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				final MainFrame frame1 = new MainFrame(4565, "b");
				frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame1.setVisible(true);
			}
		});
	}
}
