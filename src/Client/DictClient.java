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
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;
import javax.xml.ws.handler.MessageContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import StateCode.StateCode;

public class DictClient {
	String address;
	int port;
	int operationCount = 0;
	DictClientGUI ui;
	
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
	
	private JSONObject createReqJSON(int command, String word, String meaning) {
		JSONObject requestJson = new JSONObject();
		requestJson.put("command", String.valueOf(command));
		requestJson.put("word", word);
		requestJson.put("meaning", meaning);
		return requestJson;
	}
	
	private JSONObject parseResString(String res) {
		JSONObject resJSON = null;
		try {
			JSONParser parser = new JSONParser();
			resJSON = (JSONObject) parser.parse(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resJSON;
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
	
	private String[] execute(int operation, String word, String meaning) {
		int state = StateCode.FAIL;
		Socket socket = null;
		if (operation != StateCode.ADD) {
			meaning = "";
		}
		try {
				addLog(operation, word, meaning);
				socket = new Socket(address, port);
				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
				writer.writeUTF(createReqJSON(operation, word, meaning).toJSONString());
				writer.flush();
				String res = reader.readUTF();
				JSONObject resJSON = parseResString(res);
				state = Integer.parseInt(resJSON.get("state").toString());
				if (state == StateCode.SUCCESS) {
					meaning = (String) resJSON.get("meaning");
				}
				reader.close();
				writer.close();
				printResponse(state, meaning);
				
		} catch (UnknownHostException e) {
			state = StateCode.UNKNOWN_HOST;
			System.out.println("Error: UNKNOWN HOST!");
		} catch (ConnectException e) {
			state = StateCode.COLLECTIONG_REFUSED;
			System.out.println("Error: COLLECTIONG REFUSED!");
		} catch (IOException e) {
			state = StateCode.IO_ERROR;
			System.out.println("Error: I/O ERROR!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String[] resultArr = {String.valueOf(state), meaning};
		return resultArr;
	}
}
