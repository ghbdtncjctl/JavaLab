package obba;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Server extends Thread {
	private Map<String, String> bdparoles = new HashMap<String, String>();
	private Map<String, InetSocketAddress> bd = new HashMap<String, InetSocketAddress>();
	private static final int SERVER_PORT = 4567;
	private boolean authorized = false;
	private int x;
	private String username;

	@SuppressWarnings("null")
	public void run() {
		bd.put("a", new InetSocketAddress("127.0.0.1", 4566));
		bd.put("b", new InetSocketAddress("127.0.0.1", 4565));
		bdparoles.put("a", "1");
		bdparoles.put("b", "2");
		try {
			final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			while (!Thread.interrupted()) {
				final Socket socket = serverSocket.accept();
				try {
					DataInputStream in = new DataInputStream(socket
							.getInputStream());
					DataOutputStream out = new DataOutputStream(socket
							.getOutputStream());
					authorize(in, out);
					String mask = in.readUTF();
					if (mask.equals("0")) {
						if (authorized)
							sendMessage(in);
					} else if (mask.equals("1")) {
						String mask1 = in.readUTF();
						Set<String> keys = bdparoles.keySet();
						String[] args = {};
						args = keys.toArray(new String[keys.size()]);
						String res = "";
						for (String arg : args)
							if (Math.abs(mask1.compareTo(arg)) <= 1)
								res = res + arg + "1";
						out.writeUTF(res);
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JPanel(),
							"Ошибка в работе серв", "Ошибка",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					socket.close();
				}
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JPanel(),
					"Ошибка в работе сервfrf", "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private synchronized void authorize(DataInputStream in, DataOutputStream out)
			throws IOException {
		// Читаем имя отправителя
		username = in.readUTF();
		final String password = in.readUTF();
		if (!password.equals(bdparoles.get(username))) {
			out.writeUTF("0");
			authorized = false;
		} else {
			out.writeUTF("1");
			authorized = true;
		}
		;
	}

	private void sendMessage(DataInputStream in) throws IOException {
		final String targetName = in.readUTF();
		// Читаем сообщение
		InetSocketAddress targetadr = bd.get(targetName);
		if (targetadr == null)
			return;
		final String message = in.readUTF();
		// Выделяем IP-адрес
		final Socket socket2 = new Socket(/* "127.0.0.1", x */);
		try {
			socket2.connect(targetadr);
			// Записываем в поток имя
			DataOutputStream out = new DataOutputStream(socket2
					.getOutputStream());
			out.writeUTF(username);
			// Записываем в поток сообщение
			out.writeUTF(message);
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			socket2.close();
		}

	}
}
