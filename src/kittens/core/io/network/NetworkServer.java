package kittens.core.io.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import kittens.core.deck.Card;
import kittens.core.deck.Hand;
import kittens.core.players.*;

public class NetworkServer {

	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private int port;
	private Playerbase players;
	

	public NetworkServer(int port) throws IOException {
		this.port = port;
	}
	
	public Playerbase establishConnections(int numberOfPlayers, int numberOfBots) throws IOException{
		
		players = new Playerbase();
		
		ArrayList<Card> tempHand = new ArrayList<Card>();
		Hand hand = new Hand(tempHand);
		
		players.addPlayer(new Player(0, false, hand, null, null, null));	//add server

		if ( (numberOfPlayers - numberOfBots) > 0) {
			System.out.println(port);
			serverSocket = new ServerSocket(port);
		}
		
		//add bot players
		for (int i = 1; i <= numberOfBots; i++) {
			players.addPlayer(new Player(i, true, null, null, null, null));
		}

		//add human players
		for (int i = numberOfBots + 1; i < numberOfPlayers; i++){
			if(!players.tooManyPlayers()){
				try {
					connectionSocket = serverSocket.accept();
					ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
					ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());

					tempHand = new ArrayList<Card>();
					hand = new Hand(tempHand);
					players.addPlayer(new Player(i, false, hand, connectionSocket, outToClient, inFromClient));

					System.out.println("Connected to player " + i);
					outToClient.writeObject("You connected to the server as player " + i + "\n");
					
				} catch (IOException e) {
					System.out.println("Connection failed");
				}
			}
		}
		return players;
	}
}
