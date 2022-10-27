package kittens.core.players;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import kittens.core.deck.Card;
import kittens.core.deck.Hand;

public class Player {
	private int playerID;
	private boolean isBot;
	private boolean exploded = false;
	private Hand hand;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	private boolean isOnline;
	private boolean isBlind; //future expansions?
	private int turnsToTake; //users have turns to take
	private boolean isAttacked;

	public Player(int playerID, boolean isBot, Hand hand, Socket connection, ObjectOutputStream outToClient, ObjectInputStream inFromClient) {
		this.playerID = playerID;
		this.isBot = isBot;
		this.hand = hand;
		this.outToClient = outToClient;
		this.inFromClient = inFromClient;
		this.exploded = false;
		this.turnsToTake = 1; //default: 1 turn
		this.isAttacked = false; //on create: player is not attacked.
		
		if(connection == null){
			this.isOnline = false;
		} else {
			this.isOnline = true;
		}
	}

	public boolean getIsBot() {
		return isBot;
	}

	public boolean getIsOnline() {
		return isOnline;
	}
	
	public void createHand() {
		ArrayList<Card> temphand = new ArrayList<Card>();
		hand = new Hand(temphand);
	}

	public int getPlayerID() {
		return playerID;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public void sendMessage(Object message) {
		if(isOnline) {
			try {
				outToClient.writeObject(message);
			} 
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} else if (!isBot) {
			System.out.println(message);
		}
	}
	
	public boolean isExploded(){
		return exploded;
	}
	public void setExploded() {
		exploded = true;
	}

	public String getInput(boolean interruptable) {
		String word = " "; 
        if(isOnline)
            try{
            	word = (String) inFromClient.readObject();
            } catch (Exception e){
            	System.out.println("Reading from client failed: " + e.getMessage());
            }
        else
            try {
            	if(interruptable) {
				    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				    int millisecondsWaited = 0;
				    while(!br.ready() && millisecondsWaited<(5000)) {
				    	Thread.sleep(200);
				    	millisecondsWaited += 200;
				    }
				    if(br.ready())
				    	return br.readLine();               		
            	} else {
                	Scanner in = new Scanner(System.in); 
                	word=in.nextLine();
            	}
            } catch(Exception e){System.out.println(e.getMessage());}
        return word;
	}

	public int getNrTurnsToTake(){
		return turnsToTake;
	}

	public void setNrTurnsToTake(int turns){
		turnsToTake = turns;
	}

	public boolean getBlind(){
		return isBlind;
	}

	public void setBlind(boolean status){
		if(status == true){
			isBlind = true;
		} else if (status == false){
			isBlind = false;
		}
	}

	public boolean getAttackedStatus(){
		return isAttacked;
	}

	public void setAttacked(){
		isAttacked = true;
	}

	public void setNotAttacked(){
		isAttacked = false;
	}
}