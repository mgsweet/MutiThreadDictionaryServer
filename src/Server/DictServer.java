package Server;
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
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DictServer dictServer = new DictServer(args[0], args[1]);
			dictServer.run();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter <port> <dictionary-file>");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		this.server = null;
	}
	
	public void run() {
		try {
			this.server = new ServerSocket(this.port);
			printInitialStats();
			while(true) {
				Socket client = server.accept();
				numOfClient++;
				System.out.println("Server: A client connect. Current Num of client: " + String.valueOf(numOfClient));
				Thread dcThread = new Thread(new DictControlerThread(this, client, dict));
				dcThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void clientDisconnect() {
		System.out.println("Server: A client has disconnected");
		numOfClient--;
		System.out.println("Server: Number of clients: " + numOfClient + "\n");
	}
	
	public void setPort(String p) {
		port = Integer.parseInt(p);
	}
	
	public int getPort() {
		return port;
	}
}
