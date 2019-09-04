package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.sql.ClientInfoStatus;

import javax.imageio.IIOException;

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
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			int command = Integer.parseInt(reader.readLine());
			String word = reader.readLine();
			System.out.println("-- Get Request --\n  Command: " + state2String(command) + "\n  word: " + word);
			int isSuccess = StateCode.FAIL;	
			String meaning = "";
			switch (command) {
			case StateCode.QUERY:
				if (dict.isWordExist(word)) {
					meaning = dict.query(word);
					isSuccess = StateCode.SUCCESS;
				} else {
					isSuccess = StateCode.FAIL;
				}
				writer.write(String.valueOf(isSuccess) + '\n');
				writer.write(meaning);
				writer.flush();
				break;
			case StateCode.ADD:
				if (!dict.isWordExist(word)) {
					meaning = reader.readLine();
					dict.add(word, meaning);
					isSuccess = StateCode.SUCCESS;
				} else {
					System.out.println("Warning: Word exist");
					isSuccess = StateCode.FAIL;
				}
				writer.write(String.valueOf(isSuccess) + '\n');
				writer.write(meaning);
				writer.flush();
				break;
			case StateCode.REMOVE:
				if (dict.isWordExist(word)) {
					dict.remove(word);
					isSuccess = StateCode.SUCCESS;
				} else {
					isSuccess = StateCode.FAIL;
				}
				writer.write(String.valueOf(isSuccess) + '\n');
				writer.write(meaning);
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
