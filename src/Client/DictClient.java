/**
 * @author Aaron-Qiu, mgsweet@126.com, student_id:1101584
 */
package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;
import javax.xml.ws.handler.MessageContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import StateCode.StateCode;

public class DictClient {
	private String address;
	private int port;
	private int operationCount = 0;
	private DictClientGUI ui;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// Check port format.
			if (Integer.parseInt(args[1]) <= 1024 || Integer.parseInt(args[1]) >= 49151) {
				System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
				System.exit(-1);
			}
			System.out.println("Dictionary Client");
			DictClient client = new DictClient(args[0], Integer.parseInt(args[1]));
			client.run();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Lack of Parameters:\nPlease run like \"java -java DictClient.java <server-adress> <server-port>");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DictClient(String address, int port) {
		this.address = address;
		this.port = port;
		this.operationCount = 0;
		ui = null;
	}
	
	public void run() {
		try {
			this.ui = new DictClientGUI(this);
			ui.getFrame().setVisible(true);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter <server-adress> <server-port>");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addLog(int state, String word, String meaning) {
		System.out.println("--LOG: " + String.valueOf(operationCount) + " ------");
		System.out.println("  Request:");
		switch (state) {
		case StateCode.ADD:
			System.out.println("  Command: ADD");
			break;
		case StateCode.QUERY:
			System.out.println("  Command: QUERY");
			break;
		case StateCode.REMOVE:
			System.out.println("  Command: REMOVE");
			break;
		default:
			System.out.println("  Error: Unknown Command");
			break;
		}
		System.out.println("  Word: " + word);
		if (state == StateCode.ADD) System.out.println("  Meaning:\n\t" + meaning);
		operationCount++;
	}
	
	private void printResponse(int state, String meaning) {
		System.out.println("  Response:");
		switch (state) {
		case StateCode.SUCCESS:
			System.out.println("  State: SUCCESS");
			break;
		case StateCode.FAIL:
			System.out.println("  State: FAIL");
			break;
		default:
			System.out.println("  Error: Unknown State");
			break;
		}
		System.out.println("  Meaning:\n\t" + meaning);
	}
	
	public int add(String word, String meaning) {
		String[] resultArr = execute(StateCode.ADD, word, meaning);
		return Integer.parseInt(resultArr[0]);
	}
	
	public int remove(String word) {
		String[] resultArr = execute(StateCode.REMOVE, word, "");
		return Integer.parseInt(resultArr[0]);
	}
	
	public String[] query(String word) {
		String[] resultArr = execute(StateCode.QUERY, word, "");
		return resultArr;
	}
	
	private String[] execute(int command, String word, String meaning) {
		int state = StateCode.FAIL;
		addLog(command, word, meaning);
		try {
			System.out.println("Trying to connect to server...");
			ExecuteThread eThread = new ExecuteThread(address, port, command, word, meaning);
			eThread.start();
			eThread.join(2000);
			if (eThread.isAlive()) {
				eThread.interrupt();
				throw new TimeoutException();
			}
			String[] eThreadResult = eThread.getResult();
			state = Integer.parseInt(eThreadResult[0]);
			meaning = eThreadResult[1];
			System.out.println("Connect Success!");
		} catch (TimeoutException e) {
			state = StateCode.TIMEOUT;
			meaning = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		printResponse(state, meaning);
		String[] resultArr = {String.valueOf(state), meaning};
		return resultArr;
	}
}
