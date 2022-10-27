package kittens.core.io.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkClient {

	private Socket s;
	private int port = 2048; 				//default
	private String ipAddress = "127.0.0.1"; //default
	
	ObjectOutputStream outToServer;
    ObjectInputStream inFromServer;
	
	public NetworkClient(String ipAddress, int Port) {
		this.ipAddress = ipAddress;
		this.port = Port;
		try {
			openConnection();
		} catch (IOException e) {
			System.out.println("Connection failed");
		}
	}
	
	public void openConnection() throws IOException {
		s = new Socket(ipAddress, port);
		outToServer = new ObjectOutputStream(s.getOutputStream());
		inFromServer = new ObjectInputStream(s.getInputStream());
		ExecutorService threadpool = Executors.newFixedThreadPool(1);
		Runnable receive = new Runnable() {
			
			@Override
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	
        		while(true) {
        			try {
		        		String nextMessage = (String) inFromServer.readObject();
	    	    		System.out.println(nextMessage);   		
	    	    		if(nextMessage.contains("options:") || nextMessage.contains("Give") || nextMessage.contains("Steal") || nextMessage.contains("Available targets: ") || nextMessage.contains("Where in ")){ //options (your turn), Give (Opponent played Favor), Where (You defused an exploding kitten), Available targets: selecting targets
	    	    			outToServer.writeObject(br.readLine());
	    	    		} else if(nextMessage.contains("<Enter>")) { //Press <Enter> to play Nope and Interrupt
	    				    int millisecondsWaited = 0;
	    				    while(!br.ready() && millisecondsWaited<(5000)) {
	    				    	Thread.sleep(200);
	    				    	millisecondsWaited += 200;
	    				    }
	    				    if(br.ready()) {
	    				    	outToServer.writeObject(br.readLine());
	    				    }	    				    
	    				    else
	    				    	outToServer.writeObject(" ");
	    	    		}
        			} catch(Exception e) {
        				System.exit(0);
        			}
        		}
			}
		};
		threadpool.execute(receive);
	}
	
	public void SendMessage(Object message) throws IOException {
			outToServer.writeObject(message);
	}
}