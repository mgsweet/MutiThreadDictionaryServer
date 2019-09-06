/**
 * @author Aaron-Qiu, mgsweet@126.com, student_id:1101584
 */
package Server;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ServerSocketFactory;

public class DictServer {
	private int port = 4444;
	private Dictionary dict;
	private ServerSocket server;
	private int numOfClient = 0;
	private DictServerUI ui;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			if (Integer.parseInt(args[0]) <= 1024 || Integer.parseInt(args[0]) >= 49151) {
				System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
				System.exit(-1);
			}
			DictServer dictServer = new DictServer(args[0], args[1]);
			dictServer.run();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Lack of Parameters:\nPlease run like \"java - jar DictServer.jar <port> <dictionary-file>\"!");
		} catch (NumberFormatException e) {
			System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printOnBoth(String str) {
		System.out.println(str);
		if (ui != null) ui.getlogArea().append(str + '\n');
	}
	
	private void printInitialStats() throws UnknownHostException {
		InetAddress ip = InetAddress.getLocalHost();
		System.out.println("Server Running...");
		System.out.println("Current IP address : " + ip.getHostAddress());
		System.out.println("Port = " + port);	
		System.out.println("Waiting for clinet connection...\n--------------");
	}
	
	public DictServer(String p, String dithPath) {
		this.port = Integer.parseInt(p);
		this.dict = new Dictionary(dithPath);
		this.ui = null;
		this.server = null;
	}
	
	public void run() {
		try {
			this.server = new ServerSocket(this.port);
			printInitialStats();
			this.ui = new DictServerUI(InetAddress.getLocalHost().getHostAddress(), String.valueOf(port), dict.getPath());
			ui.getFrame().setVisible(true);
			while(true) {
				Socket client = server.accept();
				numOfClient++;
				printOnBoth("Server: A client connect.\n Current Num of client: " + String.valueOf(numOfClient));
				DictRequestHandlerThread dcThread = new DictRequestHandlerThread(this, client, dict);
				dcThread.start();
			}
		} catch (BindException e) {
			System.out.println("Address already in use (Bind failed), try another address!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void clientDisconnect() {
		printOnBoth("Server: A client has disconnected");
		numOfClient--;
		printOnBoth("Server: Number of clients: " + numOfClient + "\n");
	}
	
	public void setPort(String p) {
		port = Integer.parseInt(p);
	}
	
	public int getPort() {
		return port;
	}
}
