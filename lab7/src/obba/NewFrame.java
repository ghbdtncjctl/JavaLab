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
public class NewFrame extends JFrame {
	private static final String FRAME_TITLE = "Find user";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
	private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
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
	protected boolean isCreated=false;
	private String[] args={};
	Frame frame;

	public Frame getFrame(){return frame;}
	public boolean Created(){return isCreated;}

	NewFrame(final String username, final String password) {
		super(FRAME_TITLE);
		this.username=username;
		this.password=password;

		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		// Центрирование окна
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit
				.getScreenSize().height - getHeight()) / 2);
		// Текстовая область для отображения полученных сообщений
		textAreaIncoming.setEditable(false);
		// Контейнер, обеспечивающий прокрутку текстовой области
		final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
		// Поля ввода имени пользователя и адреса получателя

		// Текстовая область для ввода сообщения

		// Контейнер, обеспечивающий прокрутку текстовой области
		final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
		// Панель ввода сообщения
		final JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createTitledBorder("Search"));
		// Кнопка отправки сообщения
		final JButton sendButton = new JButton("Отправить");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		final JButton ChatButton = new JButton("Chat");
		ChatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String target=textAreaOutgoing.getText();
				if (!isCreated && contains(target,args)){
					isCreated=true;
					frame = new Frame(username,password,target);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
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
												LARGE_GAP)).addComponent(
										scrollPaneOutgoing).addComponent(
												ChatButton).addComponent(
										sendButton)).addContainerGap());
		layout2.setVerticalGroup(layout2.createSequentialGroup()
				.addContainerGap().addGroup(
						layout2.createParallelGroup(Alignment.BASELINE))
				.addGap(MEDIUM_GAP).addComponent(scrollPaneOutgoing).addGap(
						MEDIUM_GAP).addComponent(ChatButton).addGap(MEDIUM_GAP)
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
		/*
		 * if(!authorize(username,password)){ sendButton.setEnabled(false); }
		 */
	}


	protected boolean contains(String name, String[] args2) {
		// TODO Auto-generated method stub
		for(String arg:args2)
			if (arg.equals(name))
				return true;
		return false;
	}


	private void sendMessage() {
		try {
			final String message = textAreaOutgoing.getText();
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
			out.writeUTF("1");
			// Записываем в поток сообщение
			out.writeUTF(message);
			final DataInputStream in = new DataInputStream(socket
					.getInputStream());
			String a = in.readUTF();
			a = in.readUTF();
			args = a.split("1");
			// Помещаем сообщения в текстовую область вывода
			textAreaIncoming.append("users:\n");
			for (String arg : args)
				textAreaIncoming.append(arg + "\n");
			// Очищаем текстовую область ввода сообщения
			// Закрываем сокет
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(NewFrame.this,
					"Не удалось отправить сообщение: узел-адресат не найден",

					"Ошибка", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(NewFrame.this,
					"Не удалось отправить сообщение", "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
