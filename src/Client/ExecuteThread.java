package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import StateCode.StateCode;

public class ExecuteThread extends Thread {
	private int state;
	private int command;
	private String word;
	private String meaning;
	private Socket socket;
	private String address;
	private int port;
	private String[] resultArr = {"", ""};
	
	private JSONObject createReqJSON() {
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
	
	public String[] getResult() {
		return resultArr;
	}
	
	public ExecuteThread(String address, int port, int command, String word, String meaning) {
		this.address = address;
		this.port = port;
		this.state = StateCode.FAIL;
		this.command = command;
		this.word = word;
		this.meaning = meaning;
		socket = null;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);  // has problem				
			DataInputStream reader = new DataInputStream(socket.getInputStream());
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			writer.writeUTF(createReqJSON().toJSONString());
			writer.flush();
			String res = reader.readUTF();
			JSONObject resJSON = parseResString(res);
			state = Integer.parseInt(resJSON.get("state").toString());
			if (state == StateCode.SUCCESS) {
				meaning = (String) resJSON.get("meaning");
			}
			reader.close();
			writer.close();	
		} catch (UnknownHostException e) {
			state = StateCode.UNKNOWN_HOST;
			System.out.println("Error: UNKNOWN HOST!");
		} catch (ConnectException e) {
			state = StateCode.COLLECTIONG_REFUSED;
			System.out.println("Error: COLLECTIONG REFUSED!");
		} catch (SocketTimeoutException e) {
			state = StateCode.TIMEOUT;
			System.out.println("Timeoutr!");
		} catch (SocketException e) {
			state = StateCode.IO_ERROR;
			System.out.println("Error: I/O ERROR!");
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
		resultArr[0] = String.valueOf(state);
		resultArr[1] = meaning;
	}
	
}
