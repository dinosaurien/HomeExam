package kittens.core.players;

import java.util.ArrayList;

public class Playerbase {
	
	private ArrayList<Player> players = null;
	
	public Playerbase() {
		players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removePlayer(int playerid) {
		players.remove(playerid);
	}
	
	public ArrayList<Player> getPlayerbase(){
		return players;
	}

    public int getSize() {
        return players.size();
    }

	public Player getPlayer(int ID) {
		for (Player player : players){
			if (player.getPlayerID() == ID){
				return player;
			}
		}
		return null;
	}

	public boolean tooManyPlayers(){
		if(players.size() > 5){
			return true;
		} else { 
			return false;
		}
	}

	public boolean tooFewPlayers(){
		if(players.size() < 2){
			return true;
		} else {
			return false;
		}
	}
}
