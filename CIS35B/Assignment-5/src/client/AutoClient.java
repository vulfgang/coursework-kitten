/*
 * CIS 35B
 * Modified by: Wolfgang C. Strack
 * Compiler: Java 8 with Eclipse
 * 
 * This is the CreateClient class.
 */
package client;

import java.net.*;
import java.io.*;

import util.AutoException;

public class AutoClient {
	private Socket clientSocket;
	// leaving DefaultClientSocket out from here because
	// its functionality doesn't seem to fit as a helper
	// for the client
	ObjectInputStream clientIn;
	ObjectOutputStream clientOut;
	
	BufferedReader stdIn;
	
	public AutoClient() {
		// rest of variables are initialized in startClient()
		stdIn = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void startClient() throws IOException {
		clientSocket = new Socket(InetAddress.getLocalHost(), 4444);
		clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
		clientIn = new ObjectInputStream(clientSocket.getInputStream());
		
		this.performOperations(); // not a thread!
	}
	
	private void performOperations() {
		String choice = "";
		String fromServer = null;
		
		try {
			fromServer = (String) clientIn.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (!choice.equals("0")) {
			if (fromServer != null) {
				System.out.println("Server: " + fromServer);
			}
			
			try { // try to perform an operation and get server response
				printMenu();
				
				System.out.print("Enter choice: ");
				choice = stdIn.readLine();
				System.out.println();
				
				switch (choice) {
				case "0": clientOut.writeObject("exit"); break;
				case "1": performOperation(1); break;
				case "2": performOperation(2); break;
				case "3":
					SelectCarOption sco = new SelectCarOption(clientIn, clientOut, stdIn);
					sco.run();
					fromServer = null;
					
					continue;
				default: System.out.println("Invalid operation choice.\n");
				}
				
				fromServer = (String) clientIn.readObject();
			} catch (IOException ioe) {
//				ioe.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
//				cnfe.printStackTrace();
			}
			
			System.out.println(fromServer); // bye
		}
	}
	
	private void printMenu() {
		System.out.println("Select a service operation below by entering the number of your choice:\n");
		
		System.out.println("1\tSend automobile to server by default-format file");
		System.out.println("2\tSend automobile to server by Properties file");
		System.out.println("3\tSearch/Edit automobiles on server");
		System.out.println("0\tQuit\n");
	}
	
	private void performOperation(int optionChoice) {
		System.out.print("Enter name of file to send: ");
		
		try {
			String filename = stdIn.readLine();
			
			CarModelOptionsIO cmoIO = new CarModelOptionsIO();
			
			clientOut.writeObject(cmoIO.readData(filename, optionChoice));
			
			System.out.println("The file " + filename + " was sent");
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (AutoException ae) {
			ae.printStackTrace();
		}
	}
}
