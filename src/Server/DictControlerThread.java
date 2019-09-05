/**
 * @author Aaron-Qiu, mgsweet@126.com, student_id:1101584
 */
package Server;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.sql.ClientInfoStatus;

import javax.imageio.IIOException;
import javax.management.Query;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import StateCode.StateCode;

public class DictControlerThread extends Thread{
	private Dictionary dict;
	private Socket clientSocket;
	private DictServer server;
	
	private String state2String(int state) {
		String s = "UnKnown";
		switch (state) {
		case StateCode.QUERY:
			s = "QUERY";
			break;
		case StateCode.ADD:
			s = "ADD";
			break;
		case StateCode.REMOVE:
			s = "REMOVE";
			break;
		default:
			break;
		}
		return s;
	}
	
	public DictControlerThread(DictServer server, Socket client, Dictionary dict) {
		this.server = server;
		this.clientSocket = client;
		this.dict = dict;
	}
	
	private JSONObject createResJSON(int state, String meaning) {
		JSONObject requestJson = new JSONObject();
		requestJson.put("state", String.valueOf(state));
		requestJson.put("meaning", meaning);
		return requestJson;
	}
	
	private JSONObject parseReqString(String res) {
		JSONObject reqJSON = null;
		try {
			JSONParser parser = new JSONParser();
			reqJSON = (JSONObject) parser.parse(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reqJSON;
	}
	
	@Override
	public void run() {
		try {
			DataInputStream reader = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream());
			JSONObject reqJSON = parseReqString(reader.readUTF());
			int command = Integer.parseInt(reqJSON.get("command").toString());
			String word = (String) reqJSON.get("word");
			server.printOnBoth("-- Get Request --\n  Command: " + state2String(command) + "\n  word: " + word);
			int state = StateCode.FAIL;	
			String meaning = (String) reqJSON.get("meaning");
			
			switch (command) {
			case StateCode.QUERY:
				if (dict.isWordExist(word)) {
					meaning = dict.query(word);
					state = StateCode.SUCCESS;
					server.printOnBoth("QUERY SUCCESS!");
				} else {
					state = StateCode.FAIL;
					server.printOnBoth("QUERY FAIL: Word Not Exist!");
				}
				writer.writeUTF(createResJSON(state, meaning).toJSONString());
				writer.flush();
				break;
			case StateCode.ADD:
				if (!dict.isWordExist(word)) {
					dict.add(word, meaning);
					state = StateCode.SUCCESS;
					server.printOnBoth("ADD SUCCESS: " + word + "\nMeaning: " + meaning);
				} else {
					server.printOnBoth("ADD FAIL: Word Exist!");
					state = StateCode.FAIL;
				}
				writer.writeUTF(createResJSON(state, "").toJSONString());
				writer.flush();
				break;
			case StateCode.REMOVE:
				if (dict.isWordExist(word)) {
					dict.remove(word);
					state = StateCode.SUCCESS;
					server.printOnBoth("REMOVE SUCCESS: " + word);
				} else {
					state = StateCode.FAIL;
					server.printOnBoth("ADD FAIL: Word Exist!");
				}
				writer.writeUTF(createResJSON(state, "").toJSONString());
				writer.flush();
				break;
			default:
				break;
			}
			reader.close();
			writer.close();
			clientSocket.close();
			this.server.clientDisconnect();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
