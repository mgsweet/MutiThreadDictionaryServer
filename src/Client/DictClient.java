package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println("Dictionary Client");
					DictClient client = new DictClient(args[0], Integer.parseInt(args[1]));
					DictClientGUI window = new DictClientGUI(client);
					window.getFrame().setVisible(true);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Please enter <server-adress> <server-port>");
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
	
	public DictClient(String address, int port) {
		this.address = address;
		this.port = port;
		this.operationCount = 0;
	}
	
	public int add(String word, String meaning) {
		int state = StateCode.FAIL;
		try {
				addLog(StateCode.ADD, word, meaning);
				Socket socket = new Socket(address, port);
				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
				writer.writeUTF(createReqJSON(StateCode.ADD, word, meaning).toJSONString());
				writer.flush();
				String res = reader.readUTF();
				JSONObject resJSON = parseResString(res);
				state = Integer.parseInt(resJSON.get("state").toString());
				reader.close();
				writer.close();
				socket.close();
				printResponse(state, "");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public int remove(String word) {
		int state = StateCode.FAIL;
		try {
				addLog(StateCode.REMOVE, word, "");
				Socket socket = new Socket(address, port);
				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
				writer.writeUTF(createReqJSON(StateCode.REMOVE, word, "").toJSONString());
				writer.flush();
				String res = reader.readUTF();
				JSONObject resJSON = parseResString(res);
				state = Integer.parseInt(resJSON.get("state").toString());
				reader.close();
				writer.close();
				socket.close();
				printResponse(state, "");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public String[] query(String word) {
		int state = StateCode.FAIL;
		String meaning = "";
		try {
				addLog(StateCode.QUERY, word, "");
				Socket socket = new Socket(address, port);
				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
				writer.writeUTF(createReqJSON(StateCode.QUERY, word, "").toJSONString());
				writer.flush();
				String res = reader.readUTF();
				JSONObject resJSON = parseResString(res);
				state = Integer.parseInt(resJSON.get("state").toString());
				if (state == StateCode.SUCCESS) {
					meaning = (String) resJSON.get("meaning");
				}
				reader.close();
				writer.close();
				socket.close();
				printResponse(state, meaning);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] resultArr = {String.valueOf(state), meaning};
		return resultArr;
	}
}
