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
			server.printOnBoth("-- Get Request --\n  Command: " + state2String(command) + "\n  word: " + word);
			int isSuccess = StateCode.FAIL;	
			String meaning = "";
			
			switch (command) {
			case StateCode.QUERY:
				if (dict.isWordExist(word)) {
					meaning = dict.query(word);
					isSuccess = StateCode.SUCCESS;
					server.printOnBoth("QUERY SUCCESS!");
				} else {
					isSuccess = StateCode.FAIL;
					server.printOnBoth("QUERY FAIL: Word Not Exist!");
				}
				writer.write(String.valueOf(isSuccess) + '\n');
				writer.write(meaning);
				writer.flush();
				break;
			case StateCode.ADD:
				if (!dict.isWordExist(word)) {
					String temp = "";
//					Dont not wht not work!!!
//					while ((temp = reader.readLine()) != null) {
//						meaning = meaning + temp + '\n'; 
//					}
					while (true) {
						temp = reader.readLine();
						System.out.println(temp);
						if (temp.equals("EOF")) {
							break;
						} else {
							meaning = meaning + temp + '\n'; 
						}
					}
					dict.add(word, meaning);
					isSuccess = StateCode.SUCCESS;
					server.printOnBoth("ADD SUCCESS: " + word + "\nMeaning: " + meaning);
				} else {
					server.printOnBoth("ADD FAIL: Word Exist!");
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
					server.printOnBoth("REMOVE SUCCESS: " + word);
				} else {
					isSuccess = StateCode.FAIL;
					server.printOnBoth("ADD FAIL: Word Exist!");
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
